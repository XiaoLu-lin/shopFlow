package org.ysling.shopflow.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.enums.*;
import org.ysling.shopflow.db.service.*;
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


    public ShopflowOrder findOrderById(String userId , String orderId){
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.USER_ID , userId);
        wrapper.eq(ShopflowOrder.ID , orderId);
        return orderService.getOne(wrapper);
    }

    public List<ShopflowAdmin> findAdmin(String username){
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.USERNAME , username);
        return adminService.list(wrapper);
    }

    public Set<String> queryByIds(String[] roleIds) {
        Set<String> roles = new HashSet<>();
        if(roleIds.length == 0){
            return roles;
        }
        QueryWrapper<ShopflowRole> wrapper = new QueryWrapper<>();
        wrapper.in(ShopflowRole.ID , Arrays.asList(roleIds));
        wrapper.eq(ShopflowRole.ENABLED , true);
        for(ShopflowRole role : roleService.queryAll(wrapper)){
            roles.add(role.getName());
        }
        return roles;
    }

    public Set<String> queryByRoleIds(String[] roleIds) {
        Set<String> permissionSet = new HashSet<>();
        if(roleIds.length == 0){
            return permissionSet;
        }
        QueryWrapper<ShopflowPermission> wrapper = new QueryWrapper<>();
        wrapper.in(ShopflowPermission.ROLE_ID, Arrays.asList(roleIds));
        for(ShopflowPermission permission : permissionService.queryAll(wrapper)){
            permissionSet.add(permission.getPermission());
        }
        return permissionSet;
    }


    public ShopflowOrder findBySn(String userId, String orderSn){
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.USER_ID , userId);
        wrapper.eq(ShopflowOrder.ORDER_SN , orderSn);
        return orderService.getOne(wrapper , false);
    }

    public List<ShopflowGroupon> queryByRuleId(String grouponRuleId){
        QueryWrapper<ShopflowGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGroupon.RULES_ID , grouponRuleId);
        return grouponService.queryAll(wrapper);
    }

    /**
     * 查询新用户注册优惠券
     */
    public List<ShopflowCoupon> queryRegister() {
        QueryWrapper<ShopflowCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCoupon.TYPE , CouponType.TYPE_REGISTER.getStatus());
        wrapper.eq(ShopflowCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return couponService.queryAll(wrapper);
    }


    public Integer countUserAndCoupon(String userId, String couponId) {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        wrapper.eq(ShopflowCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(couponUserService.count(wrapper));
    }


    public void addCouponUser(String userId, ShopflowCoupon coupon, String couponId) {
        ShopflowCouponUser couponUser = new ShopflowCouponUser();
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
            ShopflowCouponUser couponUser = couponUserService.findById(userCouponId);
            couponUser.setStatus(CouponUserStatus.STATUS_USED.getStatus());
            couponUser.setUsedTime(LocalDateTime.now());
            couponUser.setOrderId(orderId);
            if (couponUserService.updateVersionSelective(couponUser) == 0) {
                throw new RuntimeException("优惠券使用失败");
            }
        }
    }


    public ShopflowCouponUser queryOne(String userId, String couponId) {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        wrapper.eq(ShopflowCouponUser.COUPON_ID , couponId);
        wrapper.eq(ShopflowCouponUser.STATUS , CouponUserStatus.STATUS_USABLE.getStatus());
        return couponUserService.getOne(wrapper , false);
    }

    /**
     * 查找最近十条交易记录
     * @param userId 用户id
     * @param openId 用户openId
     * @return 最近十条交易记录
     */
    public List<ShopflowDealingSlip> querySelective(String userId, String openId) {
        QueryWrapper<ShopflowDealingSlip> wrapper = dealingSlipService.startPage(new PageBody(10));
        wrapper.eq(ShopflowDealingSlip.USER_ID , userId);
        wrapper.eq(ShopflowDealingSlip.OPENID , openId);
        return dealingSlipService.list(wrapper);
    }

    /**
     * 判断订单号是否存在
     */
    public Boolean countByOrderSn(String orderSn) {
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.ORDER_SN , orderSn);
        return orderService.exists(wrapper);
    }

    public ShopflowOrderGoods findByGoodsOrderId(String orderId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.getOne(wrapper , false);
    }

    public ShopflowReward findByRewardOrderId(String orderId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.ORDER_ID , orderId);
        return rewardService.getOne(wrapper , false);
    }

    public ShopflowShare findByShareOrderId(String orderId) {
        QueryWrapper<ShopflowShare> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowShare.ORDER_ID , orderId);
        return shareService.getOne(wrapper , false);
    }

    public boolean checkExistByName(String name) {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoods.NAME , name);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return goodsService.exists(wrapper);
    }


    public ShopflowRewardTask findByRewardTaskGid(String goodsId) {
        QueryWrapper<ShopflowRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowRewardTask.GOODS_ID , goodsId);
        return rewardTaskService.getOne(wrapper , false);
    }


    public void deleteByRewardTaskGid(String goodsId) {
        QueryWrapper<ShopflowRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowRewardTask.GOODS_ID , goodsId);
        rewardTaskService.remove(wrapper);
    }

    public void deleteBySpecificationGid(String goodsId) {
        QueryWrapper<ShopflowGoodsSpecification> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoodsSpecification.GOODS_ID , goodsId);
        specificationService.remove(wrapper);
    }

    public void deleteByAttributeGid(String goodsId) {
        QueryWrapper<ShopflowGoodsAttribute> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoodsAttribute.GOODS_ID , goodsId);
        attributeService.remove(wrapper);
    }

    public void deleteByProductGid(String goodsId) {
        QueryWrapper<ShopflowGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoodsProduct.GOODS_ID , goodsId);
        productService.remove(wrapper);
    }

    public void updateProduct(String productId, String goodsSn, String goodsName, BigDecimal price, String url) {
        ShopflowCart cart = new ShopflowCart();
        cart.setPrice(price);
        cart.setPicUrl(url);
        cart.setGoodsSn(goodsSn);
        cart.setGoodsName(goodsName);
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.PRODUCT_ID , productId);
        cartService.update(cart , wrapper);
    }

    public ShopflowGrouponRules findByGrouponRulesGid(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        return grouponRulesService.getOne(wrapper , false);
    }

    public Integer countByGoodsId(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        wrapper.eq(ShopflowGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return Math.toIntExact(grouponRulesService.count(wrapper));
    }

    public ShopflowGroupon queryById(String grouponId){
        QueryWrapper<ShopflowGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGroupon.GROUPON_ID , grouponId);
        return grouponService.getOne(wrapper , false);
    }

    public List<ShopflowGroupon> queryJoinRecord(String grouponId){
        QueryWrapper<ShopflowGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGroupon.GROUPON_ID , grouponId);
        return grouponService.queryAll(wrapper);
    }

    public List<ShopflowOrderGoods> queryByOid(String orderId){
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.queryAll(wrapper);
    }

    public ShopflowOrderGoods findByOrderId(String orderId){
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.getOne(wrapper , false);
    }

    public ShopflowGroupon findByGrouponOrderId(String orderId){
        QueryWrapper<ShopflowGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGroupon.ORDER_ID , orderId);
        return grouponService.getOne(wrapper , false);
    }

    public List<ShopflowOrderGoods> queryByOrderGoodsOid(String orderId){
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return orderGoodsService.queryAll(wrapper);
    }

    public List<ShopflowCouponUser> queryByCouponUserOid(String orderId){
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.ORDER_ID , orderId);
        return couponUserService.queryAll(wrapper);
    }

    /**
     * 更新商品状态
     * @param goodsId 商品ID
     * @param status  状态
     */
    public Integer updateGoodsStatus(String goodsId, Short status) {
        ShopflowGoods goods = new ShopflowGoods();
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
        QueryWrapper<ShopflowDealingSlip> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowDealingSlip.USER_ID , userId);
        wrapper.eq(ShopflowDealingSlip.ORDER_SN , orderSn);
        wrapper.eq(ShopflowDealingSlip.DEAL_TYPE , dealType.getType());
        return dealingSlipService.exists(wrapper);
    }

    /**
     * 判断商品是否库存不足
     * @param goodsId 商品ID
     */
    public Boolean isGoodsNoStock(String goodsId) {
        QueryWrapper<ShopflowGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoodsProduct.GOODS_ID , goodsId);
        List<ShopflowGoodsProduct> goodsProducts = goodsProductService.queryAll(wrapper);
        Integer number = 0;
        for (ShopflowGoodsProduct product :goodsProducts) {
            number += product.getNumber();
        }
        return number <= 0;
    }

    public Integer countReward(String taskId){
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.TASK_ID , taskId);
        wrapper.eq(ShopflowReward.STATUS , RewardStatus.STATUS_SUCCEED);
        return Math.toIntExact(rewardService.count(wrapper));
    }

}
