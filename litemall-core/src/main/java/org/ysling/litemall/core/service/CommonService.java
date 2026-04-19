package org.ysling.litemall.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.*;
import org.ysling.litemall.db.service.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用查询
 */
@Service
public class CommonService {


    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IGoodsSpecificationService specificationService;
    @Autowired
    private IGoodsAttributeService attributeService;
    @Autowired
    private IGoodsProductService productService;
    @Autowired
    private IGrouponRulesService grouponRulesService;
    @Autowired
    private IRewardTaskService rewardTaskService;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private ICouponUserService couponUserService;
    @Autowired
    private IDealingSlipService dealingSlipService;
    @Autowired
    private IGoodsProductService goodsProductService;
    @Autowired
    private IRewardService rewardService;
    @Autowired
    private IShareService shareService;
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @Autowired
    private IGrouponService grouponService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IPermissionService permissionService;


    public LitemallOrder findOrderById(String userId , String orderId){
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.USER_ID , userId);
        wrapper.eq(LitemallOrder.ID , orderId);
        return orderService.getOne(wrapper);
    }

    public List<LitemallAdmin> findAdmin(String username){
        QueryWrapper<LitemallAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAdmin.USERNAME , username);
        return adminService.list(wrapper);
    }

    public Set<String> queryByIds(String[] roleIds) {
        Set<String> roles = new HashSet<>();
        if(roleIds.length == 0){
            return roles;
        }
        QueryWrapper<LitemallRole> wrapper = new QueryWrapper<>();
        wrapper.in(LitemallRole.ID , Arrays.asList(roleIds));
        wrapper.eq(LitemallRole.ENABLED , true);
        for(LitemallRole role : roleService.queryAll(wrapper)){
            roles.add(role.getName());
        }
        return roles;
    }

    public Set<String> queryByRoleIds(String[] roleIds) {
        Set<String> permissionSet = new HashSet<>();
        if(roleIds.length == 0){
            return permissionSet;
        }
        QueryWrapper<LitemallPermission> wrapper = new QueryWrapper<>();
        wrapper.in(LitemallPermission.ROLE_ID, Arrays.asList(roleIds));
        for(LitemallPermission permission : permissionService.queryAll(wrapper)){
            permissionSet.add(permission.getPermission());
        }
        return permissionSet;
    }


    public LitemallOrder findBySn(String userId, String orderSn){
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.USER_ID , userId);
        wrapper.eq(LitemallOrder.ORDER_SN , orderSn);
        return orderService.getOne(wrapper , false);
    }

    public List<LitemallGroupon> queryByRuleId(String grouponRuleId){
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.RULES_ID , grouponRuleId);
        return grouponService.queryAll(wrapper);
    }

    /**
     * 查询新用户注册优惠券
     */
    public List<LitemallCoupon> queryRegister() {
        QueryWrapper<LitemallCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCoupon.TYPE , CouponType.TYPE_REGISTER.getStatus());
        wrapper.eq(LitemallCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return couponService.queryAll(wrapper);
    }


    public Integer countUserAndCoupon(String userId, String couponId) {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.USER_ID , userId);
        wrapper.eq(LitemallCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(couponUserService.count(wrapper));
    }


    public void addCouponUser(String userId, LitemallCoupon coupon, String couponId) {
        LitemallCouponUser couponUser = new LitemallCouponUser();
        couponUser.setUserId(userId);
        couponUser.setCouponId(couponId);
        Short timeType = coupon.getTimeType();
        if (CouponTimeType.TIME_TYPE_TIME.equals(timeType)) {
            couponUser.setStartTime(coupon.getStartTime());
            couponUser.setEndTime(coupon.getEndTime());
        } else{
            LocalDateTime now = LocalDateTime.now();
            couponUser.setStartTime(now);
            couponUser.setEndTime(now.plusDays(coupon.getDays()));
        }
        couponUser.setAddTime(LocalDateTime.now());
        couponUser.setUpdateTime(LocalDateTime.now());
        couponUserService.add(couponUser);
    }

    /**
     * 添加优惠券使用信息
     * @param couponId 优惠券ID
     * @param userCouponId 用户优惠券ID
     * @param orderId 订单ID
     */
    public void usedCoupon(String couponId, String userCouponId, String orderId){
        // 如果couponId=0则没有优惠券，couponId=-1则不使用优惠券
        if (!"0".equals(couponId) && !"-1".equals(couponId)) {
            // 如果使用了优惠券，设置优惠券使用状态
            LitemallCouponUser couponUser = couponUserService.findById(userCouponId);
            couponUser.setStatus(CouponUserStatus.STATUS_USED.getStatus());
            couponUser.setUsedTime(LocalDateTime.now());
            couponUser.setOrderId(orderId);
            if (couponUserService.updateVersionSelective(couponUser) == 0) {
                throw new RuntimeException("优惠券使用失败");
            }
        }
    }


    public LitemallCouponUser queryOne(String userId, String couponId) {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.USER_ID , userId);
        wrapper.eq(LitemallCouponUser.COUPON_ID , couponId);
        wrapper.eq(LitemallCouponUser.STATUS , CouponUserStatus.STATUS_USABLE.getStatus());
        return couponUserService.getOne(wrapper , false);
    }

    /**
     * 查找最近十条交易记录
     * @param userId 用户id
     * @param openId 用户openId
     * @return 最近十条交易记录
     */
    public List<LitemallDealingSlip> querySelective(String userId, String openId) {
        QueryWrapper<LitemallDealingSlip> wrapper = dealingSlipService.startPage(new PageBody(10));
        wrapper.eq(LitemallDealingSlip.USER_ID , userId);
        wrapper.eq(LitemallDealingSlip.OPENID , openId);
        return dealingSlipService.list(wrapper);
    }

    /**
     * 判断订单号是否存在
     */
    public Boolean countByOrderSn(String orderSn) {
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.ORDER_SN , orderSn);
        return orderService.exists(wrapper);
    }

    public LitemallOrderGoods findByGoodsOrderId(String orderId) {
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.getOne(wrapper , false);
    }

    public LitemallReward findByRewardOrderId(String orderId) {
        QueryWrapper<LitemallReward> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallReward.ORDER_ID , orderId);
        return rewardService.getOne(wrapper , false);
    }

    public LitemallShare findByShareOrderId(String orderId) {
        QueryWrapper<LitemallShare> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallShare.ORDER_ID , orderId);
        return shareService.getOne(wrapper , false);
    }

    public boolean checkExistByName(String name) {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoods.NAME , name);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return goodsService.exists(wrapper);
    }


    public LitemallRewardTask findByRewardTaskGid(String goodsId) {
        QueryWrapper<LitemallRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        return rewardTaskService.getOne(wrapper , false);
    }


    public void deleteByRewardTaskGid(String goodsId) {
        QueryWrapper<LitemallRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        rewardTaskService.remove(wrapper);
    }

    public void deleteBySpecificationGid(String goodsId) {
        QueryWrapper<LitemallGoodsSpecification> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsSpecification.GOODS_ID , goodsId);
        specificationService.remove(wrapper);
    }

    public void deleteByAttributeGid(String goodsId) {
        QueryWrapper<LitemallGoodsAttribute> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsAttribute.GOODS_ID , goodsId);
        attributeService.remove(wrapper);
    }

    public void deleteByProductGid(String goodsId) {
        QueryWrapper<LitemallGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsProduct.GOODS_ID , goodsId);
        productService.remove(wrapper);
    }

    public void updateProduct(String productId, String goodsSn, String goodsName, BigDecimal price, String url) {
        LitemallCart cart = new LitemallCart();
        cart.setPrice(price);
        cart.setPicUrl(url);
        cart.setGoodsSn(goodsSn);
        cart.setGoodsName(goodsName);
        QueryWrapper<LitemallCart> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCart.PRODUCT_ID , productId);
        cartService.update(cart , wrapper);
    }

    public LitemallGrouponRules findByGrouponRulesGid(String goodsId) {
        QueryWrapper<LitemallGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGrouponRules.GOODS_ID , goodsId);
        return grouponRulesService.getOne(wrapper , false);
    }

    public Integer countByGoodsId(String goodsId) {
        QueryWrapper<LitemallGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGrouponRules.GOODS_ID , goodsId);
        wrapper.eq(LitemallGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return Math.toIntExact(grouponRulesService.count(wrapper));
    }

    public LitemallGroupon queryById(String grouponId){
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.GROUPON_ID , grouponId);
        return grouponService.getOne(wrapper , false);
    }

    public List<LitemallGroupon> queryJoinRecord(String grouponId){
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.GROUPON_ID , grouponId);
        return grouponService.queryAll(wrapper);
    }

    public List<LitemallOrderGoods> queryByOid(String orderId){
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.queryAll(wrapper);
    }

    public LitemallOrderGoods findByOrderId(String orderId){
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.getOne(wrapper , false);
    }

    public LitemallGroupon findByGrouponOrderId(String orderId){
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.ORDER_ID , orderId);
        return grouponService.getOne(wrapper , false);
    }

    public List<LitemallOrderGoods> queryByOrderGoodsOid(String orderId){
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.queryAll(wrapper);
    }

    public List<LitemallCouponUser> queryByCouponUserOid(String orderId){
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.ORDER_ID , orderId);
        return couponUserService.queryAll(wrapper);
    }

    /**
     * 更新商品状态
     * @param goodsId 商品ID
     * @param status  状态
     */
    public Integer updateGoodsStatus(String goodsId, Short status) {
        LitemallGoods goods = new LitemallGoods();
        goods.setId(goodsId);
        goods.setStatus(status);
        goods.setUpdateTime(LocalDateTime.now());
        return goodsService.updateSelective(goods);
    }

    /**
     * 判断outBatchNo是否存在
     * @param userId  用户id
     * @param orderSn 订单编号
     * @param dealType 交易类型
     * @return true
     */
    public Boolean isDealingSlip(String userId, String orderSn, DealType dealType) {
        QueryWrapper<LitemallDealingSlip> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallDealingSlip.USER_ID , userId);
        wrapper.eq(LitemallDealingSlip.ORDER_SN , orderSn);
        wrapper.eq(LitemallDealingSlip.DEAL_TYPE , dealType.getType());
        return dealingSlipService.exists(wrapper);
    }

    /**
     * 判断商品是否库存不足
     * @param goodsId 商品ID
     */
    public Boolean isGoodsNoStock(String goodsId) {
        QueryWrapper<LitemallGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsProduct.GOODS_ID , goodsId);
        List<LitemallGoodsProduct> goodsProducts = goodsProductService.queryAll(wrapper);
        Integer number = 0;
        for (LitemallGoodsProduct product :goodsProducts) {
            number += product.getNumber();
        }
        return number <= 0;
    }

    public Integer countReward(String taskId){
        QueryWrapper<LitemallReward> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallReward.TASK_ID , taskId);
        wrapper.eq(LitemallReward.STATUS , RewardStatus.STATUS_SUCCEED);
        return Math.toIntExact(rewardService.count(wrapper));
    }

}
