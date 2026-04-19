package org.ysling.litemall.core.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.system.enums.SystemConfig;
import org.ysling.litemall.core.system.enums.SystemMallConfig;
import org.ysling.litemall.core.system.enums.SystemOrderConfig;
import org.ysling.litemall.core.tasks.impl.OrderUnpaidTask;
import org.ysling.litemall.core.tasks.service.TaskService;
import org.ysling.litemall.core.weixin.service.SubscribeMessageService;
import org.ysling.litemall.db.service.*;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.enums.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ysling
 */
@Service
public class OrderCoreService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ICouponUserService couponUserService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IUserService userService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;
    @Autowired
    private IGoodsProductService goodsProductService;
    @Autowired
    private IShareService shareService;
    @Autowired
    private IGrouponService grouponService;
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @Autowired
    private IRewardService rewardService;
    @Autowired
    private RewardCoreService rewardCoreService;
    @Autowired
    private DealingSlipCoreService slipCoreService;
    @Autowired
    private GrouponCoreService grouponCoreService;
    @Autowired
    private NotifyCoreService notifyCoreService;
    @Autowired
    private IGrouponRulesService grouponRulesService;
    @Autowired
    private IGoodsProductService productService;
    @Autowired
    private ICartService cartService;
    @Autowired
    private CommonService commonService;



    /**
     * 支付成功
     * @param order 订单
     */
    public void orderPaySuccess(LitemallOrder order){
        //有赏金更新赏金
        LitemallReward reward = commonService.findByRewardOrderId(order.getId());
        if (reward != null){
            rewardCoreService.updateRewardStatus(reward);
        }

        //更新分享记录
        LitemallShare share = commonService.findByShareOrderId(order.getId());
        if (share != null){
            share.setStatus(ShareStatus.STATUS_SUCCEED.getStatus());
            if (shareService.updateVersionSelective(share) == 0){
                throw new RuntimeException("分享记录更新失败");
            }
        }

        // 支付成功，有团购信息，更新团购信息
        LitemallGroupon groupon = commonService.findByGrouponOrderId(order.getId());
        if (groupon != null) {
            grouponCoreService.updateGrouponStatus(groupon);
        }else {
            if (order.getActualPrice().compareTo(BigDecimal.ZERO) <= 0 || !SystemConfig.isAutoWxPay()) {
                //跟新订单状态
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus(OrderStatus.STATUS_BTL_PAY.getStatus());
                if (orderService.updateVersionSelective(order) == 0){
                    throw new RuntimeException("订单更新失败，请刷新重试");
                }
            }else {
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus(OrderStatus.STATUS_PAY.getStatus());
                if (orderService.updateVersionSelective(order) <= 0) {
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
            }
            //给商家发送邮件
            notifyCoreService.orderNotify(order);
        }

        // 取消订单超时未支付任务
        taskService.removeTask(new OrderUnpaidTask(order));
    }


    /**
     * 添加订单信息和订单商品信息
     */
    public void addOrderAndOrderGoods(LitemallCart cartGoods, LitemallOrder order, LitemallUser user, Boolean isDeduction) {
        // 商品总价 = (商品数量 * 商品单价)
        BigDecimal checkedGoodsPrice = cartGoods.getPrice().multiply(BigDecimal.valueOf(cartGoods.getNumber()));

        // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
        BigDecimal freightPrice = BigDecimal.valueOf(0);
        if (checkedGoodsPrice.compareTo(SystemOrderConfig.getFreightMin()) < 0) {
            freightPrice = SystemOrderConfig.getFreightValue();
        }

        // 优惠券金额 = (优惠券减免金额 / 选中商品种类)
        BigDecimal couponPrice = order.getCouponPrice();

        // 团购优惠 = (商品数量 * 团购优惠单价)
        BigDecimal grouponPrice = order.getGrouponPrice().multiply(BigDecimal.valueOf(cartGoods.getNumber()));

        // 订单金额 = (选中商品价格 + 运费 - 团购优惠)
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).max(BigDecimal.valueOf(0));

        // 最终支付费用 = (订单金额 - 优惠券减免 - 余额)
        BigDecimal actualPrice = orderTotalPrice.subtract(couponPrice).subtract(grouponPrice).max(BigDecimal.valueOf(0));

        // 余额减免
        BigDecimal integralPrice = BigDecimal.valueOf(0);
        if (isDeduction != null && isDeduction){
            if (slipCoreService.isDeduction(user) == null){
                BigDecimal userIntegral = user.getIntegral();
                if (actualPrice.compareTo(userIntegral) >= 0){
                    actualPrice = actualPrice.subtract(userIntegral);
                    integralPrice = userIntegral;
                }else {
                    integralPrice = actualPrice;
                    actualPrice = BigDecimal.valueOf(0);
                }
                slipCoreService.subtractIntegral(user, order.getOrderSn(), integralPrice, DealType.TYPE_ORDER);
            }
        }

        order.setCouponPrice(couponPrice);
        order.setGrouponPrice(grouponPrice);
        order.setGoodsPrice(checkedGoodsPrice);
        order.setFreightPrice(freightPrice);
        order.setIntegralPrice(integralPrice);
        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);
        // 添加订单表项
        orderService.add(order);

        // 订单商品
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setOrderId(order.getId());
        BeanUtil.copyProperties(cartGoods, orderGoods);
        orderGoods.setTenantId(null);
        orderGoods.setAddTime(LocalDateTime.now());
        orderGoodsService.add(orderGoods);
    }

    /**
     * 增加库存
     * @param productId 货品ID
     * @param number    增加数量
     */
    public void addStock(String productId , Short number){
        LitemallGoodsProduct product = goodsProductService.findById(productId);
        product.setNumber(product.getNumber() + number);
        //库存减少条件 number >= number
        QueryWrapper<LitemallGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsProduct.ID , productId);
        if (!goodsProductService.update(product , wrapper)){
            throw new RuntimeException("库存增加失败");
        }
    }

    /**
     * 减少库存
     * @param productId 货品ID
     * @param number    减少数量
     */
    public void reduceStock(String productId , Short number){
        LitemallGoodsProduct product = goodsProductService.findById(productId);
        product.setNumber(product.getNumber() - number);
        //库存减少条件 number >= number
        QueryWrapper<LitemallGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsProduct.ID , productId);
        wrapper.ge(LitemallGoodsProduct.NUMBER , number);
        if (!goodsProductService.update(product , wrapper)){
            throw new RuntimeException("库存减少失败");
        }
    }

    /**
     * 扣减库存
     * @param cartGoods 购物车商品
     */
    public void reduceStock(LitemallCart cartGoods){
        // 商品货品数量减少
        String productId = cartGoods.getProductId();
        LitemallGoodsProduct product = productService.findById(productId);
        if (product.getNumber() < cartGoods.getNumber()) {
            throw new RuntimeException("库存不足");
        }

        //减少库存
        reduceStock(productId, cartGoods.getNumber());

        // 删除购物车里面的商品信息
        if (cartService.deleteById(cartGoods.getId()) <= 0){
            throw new RuntimeException("购物车删除失败");
        }

        //判断商品库存是否为0,如果是则下架商品
        if (commonService.isGoodsNoStock(cartGoods.getGoodsId())){
            Short status = GoodsStatus.GOODS_UNSOLD.getStatus();
            if (commonService.updateGoodsStatus(cartGoods.getGoodsId(), status) <= 0){
                throw new RuntimeException("商品下架失败");
            }
        }
    }


    /**
     * 返还订单 如货品数量增加，优惠券返还，余额返还
     * @param order 订单
     */
    public void orderRelease(LitemallOrder order) {
        // 商品货品数量增加
        List<LitemallOrderGoods> orderGoodsList = commonService.queryByOrderGoodsOid(order.getId());
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            //判断商品库存是否为0,如果是则上架商品
            Short number = orderGoods.getNumber();
            String productId = orderGoods.getProductId();
            addStock(productId, number);
            //商品上架
            LitemallGoods goods = goodsService.findById(orderGoods.getGoodsId());
            if (GoodsStatus.getIsUnsold(goods)){
                goods.setStatus(GoodsStatus.GOODS_ON_SALE.getStatus());
                if (goodsService.updateVersionSelective(goods) == 0){
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
            }
        }

        //设置分享取消
        LitemallShare share = commonService.findByShareOrderId(order.getId());
        if (share != null){
            share.setStatus(ShareStatus.STATUS_CANCEL.getStatus());
            if (shareService.updateVersionSelective(share) == 0){
                throw new RuntimeException("分享记录更新失败");
            }
        }

        //设置赏金取消
        LitemallReward reward = commonService.findByRewardOrderId(order.getId());
        if (reward != null){
            reward.setStatus(RewardStatus.STATUS_CANCEL.getStatus());
            if (rewardService.updateVersionSelective(reward) <= 0){
                throw new RuntimeException("网络繁忙，请刷新重试");
            }
        }

        //设置团购取消状态
        LitemallGroupon groupon = commonService.findByGrouponOrderId(order.getId());
        if(groupon != null){
            LitemallGrouponRules grouponRules = grouponRulesService.findById(groupon.getRulesId());
            if (!grouponRules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_ON.getStatus())){
                if(order.getActualPrice().compareTo(BigDecimal.ZERO) <= 0){
                    //团购未付款或订单金额为零，设置团购取消
                    groupon.setStatus(GrouponStatus.STATUS_CANCEL.getStatus());
                    if (grouponService.updateVersionSelective(groupon) == 0) {
                        throw new RuntimeException("网络繁忙，请刷新重试");
                    }
                } else if(groupon.getStatus().equals(GrouponStatus.STATUS_ON.getStatus())){
                    // 如果团购进行中，团购设置团购失败等待退款状态
                    groupon.setStatus(GrouponStatus.STATUS_FAIL.getStatus());
                    if (grouponService.updateVersionSelective(groupon) == 0) {
                        throw new RuntimeException("网络繁忙，请刷新重试");
                    }
                }
            }
        }

        //返还余额
        BigDecimal integralPrice = order.getIntegralPrice();
        if (integralPrice.compareTo(BigDecimal.ZERO) > 0){
            LitemallUser user = userService.findById(order.getUserId());
            if (user == null) {
                throw new RuntimeException("未找到用户");
            }
            //添加余额使用记录
            DealType typeOrderCancel = DealType.TYPE_ORDER_CANCEL;
            if (!commonService.isDealingSlip(user.getId(), order.getOrderSn(), typeOrderCancel)){
                slipCoreService.addIntegral(user, order.getOrderSn(), integralPrice, typeOrderCancel);
            }
        }

        // 返还优惠券
        List<LitemallCouponUser> couponUsers = commonService.queryByCouponUserOid(order.getId());
        for (LitemallCouponUser couponUser: couponUsers) {
            // 优惠券状态设置为可使用
            couponUser.setStatus(CouponUserStatus.STATUS_USABLE.getStatus());
            couponUser.setUpdateTime(LocalDateTime.now());
            if (couponUserService.updateVersionSelective(couponUser) == 0) {
                throw new RuntimeException("优惠券返还失败");
            }
        }

        //退款则发送退款订阅信息
        if (OrderStatus.isRefund(order) && order.getActualPrice().compareTo(BigDecimal.ZERO) > 0){
            //订单退款订阅通知
            LitemallUser user = userService.findById(order.getUserId());
            subscribeMessageService.refundSubscribe(user.getOpenid(), order);
            //记录操作日志
            ActionLogHandler.logOrderSucceed("订单退款", "订单编号 " + order.getOrderSn());
        }

    }




}
