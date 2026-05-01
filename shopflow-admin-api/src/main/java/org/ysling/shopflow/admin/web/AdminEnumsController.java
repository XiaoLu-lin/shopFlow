package org.ysling.shopflow.admin.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.weixin.enums.FailReasonStatus;
import org.ysling.shopflow.core.weixin.enums.MsgSecCheckType;
import org.ysling.shopflow.core.weixin.enums.TransferStatus;
import org.ysling.shopflow.db.enums.*;
import java.util.Arrays;

/**
 * 通用枚举列表
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/enums")
@Validated
public class AdminEnumsController {


    /**
     * 系统角色类型
     */
    @SaCheckPermission("admin:config:group")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "配置分组")
    @GetMapping("/configGroup")
    public Object getConfigGroup() {
        return ResponseUtil.okList(SystemUtils.getConfigGroups());
    }

    /**
     * 售后状态
     */
    @SaCheckPermission("admin:enums:aftersaleStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "售后状态")
    @GetMapping("/aftersaleStatus")
    public Object getAftersaleStatus() {
        return ResponseUtil.okList(Arrays.asList(AftersaleStatus.values()));
    }

    /**
     * 售后类型
     */
    @SaCheckPermission("admin:enums:aftersaleType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "售后类型")
    @GetMapping("/aftersaleType")
    public Object getAftersaleType() {
        return ResponseUtil.okList(Arrays.asList(AftersaleType.values()));
    }

    /**
     * 店铺类型
     */
    @SaCheckPermission("admin:enums:brandStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "店铺类型")
    @GetMapping("/brandStatus")
    public Object getBrandStatus() {
        return ResponseUtil.okList(Arrays.asList(BrandStatus.values()));
    }

    /**
     * 收藏类型
     */
    @SaCheckPermission("admin:enums:collectType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "收藏类型")
    @GetMapping("/collectType")
    public Object getCollectType() {
        return ResponseUtil.okList(Arrays.asList(CollectType.values()));
    }

    /**
     * 评论类型
     */
    @SaCheckPermission("admin:enums:commentType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "评论类型")
    @GetMapping("/commentType")
    public Object getCommentType() {
        return ResponseUtil.okList(Arrays.asList(CommentType.values()));
    }

    /**
     * 优惠券使用范围
     */
    @SaCheckPermission("admin:enums:couponGoodsType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "优惠券使用范围")
    @GetMapping("/couponGoodsType")
    public Object getCouponGoodsType() {
        return ResponseUtil.okList(Arrays.asList(CouponGoodsType.values()));
    }

    /**
     * 优惠券状态
     */
    @SaCheckPermission("admin:enums:couponStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "优惠券状态")
    @GetMapping("/couponStatus")
    public Object getCouponStatus() {
        return ResponseUtil.okList(Arrays.asList(CouponStatus.values()));
    }

    /**
     * 优惠券过期类型
     */
    @SaCheckPermission("admin:enums:couponTimeType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "优惠券过期类型")
    @GetMapping("/couponTimeType")
    public Object getCouponTimeType() {
        return ResponseUtil.okList(Arrays.asList(CouponTimeType.values()));
    }

    /**
     * 优惠券类型
     */
    @SaCheckPermission("admin:enums:couponType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "优惠券类型")
    @GetMapping("/couponType")
    public Object getCouponType() {
        return ResponseUtil.okList(Arrays.asList(CouponType.values()));
    }

    /**
     * 用户优惠券状态
     */
    @SaCheckPermission("admin:enums:couponUserStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "用户优惠券状态")
    @GetMapping("/couponUserStatus")
    public Object getCouponUserStatus() {
        return ResponseUtil.okList(Arrays.asList(CouponUserStatus.values()));
    }

    /**
     * 交易记录类型
     */
    @SaCheckPermission("admin:enums:dealType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "交易记录类型")
    @GetMapping("/dealType")
    public Object getDealType() {
        return ResponseUtil.okList(Arrays.asList(DealType.values()));
    }

    /**
     * 转账失败状态
     */
    @SaCheckPermission("admin:enums:failReasonStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "转账失败状态")
    @GetMapping("/failReasonStatus")
    public Object getFailReasonStatus() {
        return ResponseUtil.okList(Arrays.asList(FailReasonStatus.values()));
    }

    /**
     * 敏感词类型
     */
    @SaCheckPermission("admin:enums:msgSecCheckType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "敏感词类型")
    @GetMapping("/msgSecCheckType")
    public Object getMsgSecCheckType() {
        return ResponseUtil.okList(Arrays.asList(MsgSecCheckType.values()));
    }

    /**
     * 转账状态
     */
    @SaCheckPermission("admin:enums:transferStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "转账状态")
    @GetMapping("/transferStatus")
    public Object getTransferStatus() {
        return ResponseUtil.okList(Arrays.asList(TransferStatus.values()));
    }

    /**
     * 商品状态
     */
    @SaCheckPermission("admin:enums:goodsStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "商品状态")
    @GetMapping("/goodsStatus")
    public Object getGoodsStatus() {
        return ResponseUtil.okList(Arrays.asList(GoodsStatus.values()));
    }

    /**
     * 团购规则状态
     */
    @SaCheckPermission("admin:enums:grouponRuleStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "团购规则状态")
    @GetMapping("/grouponRuleStatus")
    public Object getGrouponRuleStatus() {
        return ResponseUtil.okList(Arrays.asList(GrouponRuleStatus.values()));
    }

    /**
     * 团购参与状态
     */
    @SaCheckPermission("admin:enums:grouponStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "团购参与状态")
    @GetMapping("/grouponStatus")
    public Object getGrouponStatus() {
        return ResponseUtil.okList(Arrays.asList(GrouponStatus.values()));
    }

    /**
     * 点赞类型
     */
    @SaCheckPermission("admin:enums:likeType")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "点赞类型")
    @GetMapping("/likeType")
    public Object getLikeType() {
        return ResponseUtil.okList(Arrays.asList(LikeType.values()));
    }

    /**
     * 订单状态
     */
    @SaCheckPermission("admin:enums:orderStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "订单状态")
    @GetMapping("/orderStatus")
    public Object getOrderStatus() {
        return ResponseUtil.okList(Arrays.asList(OrderStatus.values()));
    }

    /**
     * 赏金参与状态
     */
    @SaCheckPermission("admin:enums:rewardStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "赏金参与状态")
    @GetMapping("/rewardStatus")
    public Object getRewardStatus() {
        return ResponseUtil.okList(Arrays.asList(RewardStatus.values()));
    }

    /**
     * 赏金规则状态
     */
    @SaCheckPermission("admin:enums:rewardTaskStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "赏金规则状态")
    @GetMapping("/rewardTaskStatus")
    public Object getRewardTaskStatus() {
        return ResponseUtil.okList(Arrays.asList(RewardTaskStatus.values()));
    }

    /**
     * 分享状态
     */
    @SaCheckPermission("admin:enums:shareStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "分享状态")
    @GetMapping("/shareStatus")
    public Object getShareStatus() {
        return ResponseUtil.okList(Arrays.asList(ShareStatus.values()));
    }

    /**
     * 用户性别
     */
    @SaCheckPermission("admin:enums:userGender")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "用户性别")
    @GetMapping("/userGender")
    public Object getUserGender() {
        return ResponseUtil.okList(Arrays.asList(UserGender.values()));
    }

    /**
     * 用户账号等级
     */
    @SaCheckPermission("admin:enums:userLevel")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "用户账号等级")
    @GetMapping("/userLevel")
    public Object getUserLevel() {
        return ResponseUtil.okList(Arrays.asList(UserLevel.values()));
    }

    /**
     * 用户账号状态
     */
    @SaCheckPermission("admin:enums:userStatus")
    @RequiresPermissionsDesc(menu = {"系统管理", "枚举管理"}, button = "用户账号状态")
    @GetMapping("/userStatus")
    public Object getUserStatus() {
        return ResponseUtil.okList(Arrays.asList(UserStatus.values()));
    }

}
