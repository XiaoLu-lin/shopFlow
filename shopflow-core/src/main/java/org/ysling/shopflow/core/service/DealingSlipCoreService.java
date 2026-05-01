package org.ysling.shopflow.core.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.handler.ActionLogHandler;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.enums.SystemConfig;
import org.ysling.shopflow.core.utils.http.GlobalWebUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.utils.ip.IpUtil;
import org.ysling.shopflow.core.weixin.enums.TransferStatus;
import org.ysling.shopflow.db.service.*;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.db.enums.DealType;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户余额交易记录
 * @author Ysling
 */
@Slf4j
@Service
public class DealingSlipCoreService {
    
    @Autowired
    private IUserService userService;
    @Autowired
    private IDealingSlipService dealingSlipService;
    @Autowired
    private CommonService commonService;


    /**
     * 判断用户余额是否可以交易
     * @param userDeal 用户信息
     * @return null --> true
     */
    public Object isDeduction(ShopflowUser userDeal){
        ShopflowUser user = userService.findById(userDeal.getId());
        if (user == null){
            return ResponseUtil.fail("用户不存在");
        }

        if (user.getIntegral().compareTo(BigDecimal.ZERO) <= 0){
            return ResponseUtil.fail("余额不足");
        }

        //最近十次交易记录
        List<ShopflowDealingSlip> dealingSlipList = commonService.querySelective(user.getId(), user.getOpenid());
        if (dealingSlipList.size() <= 0){
            return ResponseUtil.fail("资金异常");
        }

        BigDecimal lastBalance = dealingSlipList.get(0).getBalance();
        if (user.getIntegral().compareTo(lastBalance) != 0){
            return ResponseUtil.fail("资金异常");
        }

        //判断最近十次交易记录是否正常
        for (ShopflowDealingSlip dealingSlip :dealingSlipList) {
            BigDecimal balance = dealingSlip.getBalance();
            if (lastBalance.compareTo(balance) != 0){
                return ResponseUtil.fail("资金异常");
            }
            BigDecimal award = dealingSlip.getAward();
            lastBalance = lastBalance.subtract(award);
            Short dealType = dealingSlip.getDealType();
            Short orderType = DealType.TYPE_ORDER.getType();
            Short cancelType = DealType.TYPE_ORDER_CANCEL.getType();
            if (dealType.equals(orderType) || dealType.equals(cancelType)){
                if (!commonService.countByOrderSn(dealingSlip.getOrderSn())){
                    return ResponseUtil.fail("资金异常");
                }
            }
        }
        return null;
    }


    /**
     * 添加系统修改金额交易记录
     * @param user 用户
     */
    public void systemIntegralUpdate(ShopflowUser user){
        ShopflowUser dealUser = userService.findById(user.getId());
        BigDecimal integral = user.getIntegral();
        BigDecimal dealIntegral = dealUser.getIntegral();
        if (integral.compareTo(dealIntegral) == 0){
            return;
        }
        ShopflowDealingSlip dealingSlip = new ShopflowDealingSlip();
        dealingSlip.setBalance(integral);
        dealingSlip.setAward(integral.subtract(dealIntegral));
        dealingSlip.setStatus(TransferStatus.SUCCESS.getStatus());
        dealingSlip.setDealType(DealType.TYPE_SYSTEM.getType());
        dealingSlip.setTenantId(user.getTenantId());
        this.updateIntegral(user, dealingSlip);
        ActionLogHandler.logGeneralSucceed("手动修改用户余额",String.format("余额：%s-->%s",dealIntegral,integral));
    }


    /**
     * 订单修改余额交易记录
     * @param order 订单
     * @param brand 店铺
     */
    public void addOrderIntegral(ShopflowOrder order ,ShopflowBrand brand){
        ShopflowUser brandUser = this.isAddOrderIntegral(order , brand);
        if (brandUser == null){
            return;
        }
        // 获取订单商品信息
        ShopflowOrderGoods orderGoods = commonService.findByGoodsOrderId(order.getId());
        if (orderGoods == null){
            return;
        }
        //优惠券金额
        BigDecimal couponPrice = order.getCouponPrice();
        //余额金额
        BigDecimal integralPrice = order.getIntegralPrice();
        //付款金额
        BigDecimal actualPrice = order.getActualPrice();
        //订单金额
        BigDecimal orderPrice = actualPrice.add(couponPrice).add(integralPrice);

        //添加赏金余额
        ShopflowReward reward = commonService.findByRewardOrderId(order.getId());
        if (reward != null){
            ShopflowUser creatorUser = userService.findById(reward.getCreatorUserId());
            // 赏金奖励 * 商品数量
            BigDecimal award = reward.getAward().multiply(BigDecimal.valueOf(orderGoods.getNumber()));
            // 添加余额
            this.addIntegral(creatorUser, order.getOrderSn(), award, DealType.TYPE_REWARD);
            // 从订单金额中扣除赏金金额
            orderPrice = orderPrice.subtract(award);
        }

        //添加分享余额
        ShopflowShare share = commonService.findByShareOrderId(order.getId());
        if (share != null){
            ShopflowUser inviterUser = userService.findById(share.getInviterId());
            // 添加余额
            this.addIntegral(inviterUser, order.getOrderSn(), share.getAward(), DealType.TYPE_SHARE);
        }

        ShopflowUser user = userService.findById(brandUser.getId());
        //获取系统服务费比例 1%
        BigDecimal orderBrokerage = SystemConfig.getOrderBrokerage();
        //将比例转为小数 0.01
        orderBrokerage = orderBrokerage.divide(BigDecimal.valueOf(100) , 2 , RoundingMode.HALF_UP);
        //订单金额 - （服务费比例 * 订单金额）
        orderPrice = orderPrice.subtract(orderBrokerage.multiply(orderPrice));
        //添加订单余额
        this.addIntegral(user, order.getOrderSn(), orderPrice , DealType.TYPE_ORDER);
    }


    /**
     * 添加余额
     * @param user 用户信息
     * @param award 添加金额
     * @param dealType 交易类型
     */
    public void addIntegral(ShopflowUser user, BigDecimal award, DealType dealType){
        this.addIntegral(user, null, award , dealType);
    }

    /**
     * 添加余额
     * @param user 用户信息
     * @param orderSn 订单编号
     * @param award 添加金额
     * @param dealType 交易类型
     */
    public void addIntegral(ShopflowUser user , String orderSn, BigDecimal award , DealType dealType){
        //用户积分加上金额
        ShopflowDealingSlip dealingSlip = new ShopflowDealingSlip();
        dealingSlip.setAward(award);
        dealingSlip.setOrderSn(orderSn);
        dealingSlip.setBalance(user.getIntegral().add(award));
        dealingSlip.setStatus(TransferStatus.SUCCESS.getStatus());
        dealingSlip.setDealType(dealType.getType());
        this.updateIntegral(user, dealingSlip);
    }

    /**
     * 减少余额
     * @param user      用户信息
     * @param orderSn   订单编号
     * @param award     减少金额
     * @param dealType  交易类型
     */
    public void subtractIntegral(ShopflowUser user ,String orderSn, BigDecimal award , DealType dealType){
        //用户积分减去金额
        ShopflowDealingSlip dealingSlip = new ShopflowDealingSlip();
        dealingSlip.setAward(award.negate());
        dealingSlip.setOrderSn(orderSn);
        dealingSlip.setBalance(user.getIntegral().subtract(award));
        dealingSlip.setStatus(TransferStatus.SUCCESS.getStatus());
        dealingSlip.setDealType(dealType.getType());
        this.updateIntegral(user , dealingSlip);
    }

    /**
     * 余额提现，减少余额并添加交易记录
     * @param user            用户信息
     * @param award           交易金额
     * @param outTradeNo      交易类型
     */
    public void subtractIntegral(ShopflowUser user, BigDecimal award, String outTradeNo){
        //用户积分减去金额
        ShopflowDealingSlip dealingSlip = new ShopflowDealingSlip();
        dealingSlip.setAward(award.negate());
        dealingSlip.setOutBatchNo(outTradeNo);
        dealingSlip.setBalance(user.getIntegral().subtract(award));
        dealingSlip.setStatus(TransferStatus.PROCESSING.getStatus());
        dealingSlip.setDealType(DealType.TYPE_DRAW_MONEY.getType());
        this.updateIntegral(user , dealingSlip);
    }


    /**
     * 更新余额
     * @param user 用户信息
     * @param dealingSlip 交易记录
     */
    public void updateIntegral(ShopflowUser user, ShopflowDealingSlip dealingSlip){
        if (dealingSlip.getBalance() == null){
            throw new RuntimeException("交易后余额不能为空");
        }
        HttpServletRequest request = GlobalWebUtil.getRequest();
        dealingSlip.setId(null);
        dealingSlip.setUserId(user.getId());
        dealingSlip.setOpenid(user.getOpenid());
        dealingSlip.setBatchTime(LocalDateTime.now());
        dealingSlip.setLastDealIp(IpUtil.getIpAddr(request));
        if (dealingSlipService.add(dealingSlip) == 0){
            throw new RuntimeException("交易记录添加失败,请重试");
        }
        //减少用户余额
        user.setIntegral(dealingSlip.getBalance());
        if (userService.updateVersionSelective(user) <= 0){
            throw new RuntimeException("用户余额更新失败,请重试");
        }
    }


    /**
     * 判断是否可以执行打款操作
     * @param brand 店铺信息
     * @param order 订单信息
     * @return      店铺用户
     */
    private ShopflowUser isAddOrderIntegral(ShopflowOrder order , ShopflowBrand brand){
        //获取店铺所有者向店家打款
        if (brand == null || brand.getUserId() == null || brand.getUserId().equals("0")) {
            log.info("{发放交易余额}{未找到店铺--订单id:" + order.getId() +"}");
            ActionLogHandler.logOrderFail("发放交易余额","未找到店铺--订单id:" + order.getId());
            return null;
        }

        //判断订单是否属实
        if (!brand.getId().equals(order.getBrandId())){
            log.info("{发放交易余额}{当前订单不属于该店铺:" + order.getOrderSn() +"}");
            ActionLogHandler.logOrderFail("发放交易余额","当前订单不属于该店铺:"+order.getOrderSn());
            throw new RuntimeException("当前订单不属于该店铺");
        }

        //获取店铺用户
        ShopflowUser user = userService.findById(brand.getUserId());
        if (!ObjectUtils.allNotNull(user , user.getTrueName() , user.getOpenid())) {
            log.info("{发放交易余额}{店家信息不正确:" + brand.getId() +"}");
            ActionLogHandler.logOrderFail("发放交易余额","店家信息不正确:"+brand.getId());
            throw new RuntimeException("店家信息不正确");
        }

        return user;
    }

}
