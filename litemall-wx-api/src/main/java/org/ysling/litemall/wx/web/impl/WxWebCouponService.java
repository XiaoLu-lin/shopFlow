package org.ysling.litemall.wx.web.impl;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.service.CommonService;
import org.ysling.litemall.core.service.CouponVerifyService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallCart;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.CouponStatus;
import org.ysling.litemall.db.enums.CouponType;
import org.ysling.litemall.wx.model.coupon.body.CouponListBody;
import org.ysling.litemall.wx.model.coupon.result.CouponResult;
import org.ysling.litemall.wx.service.WxCartService;
import org.ysling.litemall.wx.service.WxCouponService;
import org.ysling.litemall.wx.service.WxCouponUserService;
import java.util.List;
import java.util.Objects;

/**
 * 优惠券服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebCouponService {

    @Autowired
    private WxCouponService couponService;
    @Autowired
    private WxCouponUserService couponUserService;
    @Autowired
    private WxCartService cartService;
    @Autowired
    private CouponVerifyService couponVerifyService;
    @Autowired
    private CommonService commonService;

    /**
     * 优惠券列表
     */
    public Object list(PageBody body) {
        return ResponseUtil.okList(couponService.queryList(body));
    }

    /**
     * 个人优惠券列表
     */
    public Object user(String userId, CouponListBody body) {
        List<LitemallCouponUser> couponUserList = couponUserService.queryList(userId, body);
        List<CouponResult> couponVoList = couponService.change(couponUserList);
        return ResponseUtil.okList(couponVoList, couponUserList);
    }

    /**
     * 当前购物车下单商品订单可用优惠券
     */
    public Object selectList(String userId, String cartId) {
        if (Objects.isNull(userId)) {
            return ResponseUtil.unlogin();
        }
        //选中的商品
        List<LitemallCart> checkedGoodsList  = cartService.getCheckedGoods(userId, cartId);
        if (checkedGoodsList == null) {
            return ResponseUtil.badArgument();
        }
        // 计算优惠券可用情况
        List<LitemallCouponUser> couponUserList = couponUserService.queryAll(userId);
        List<CouponResult> couponVoList = couponService.change(couponUserList);
        for (CouponResult cv : couponVoList) {
            LitemallCoupon coupon = couponVerifyService.checkCoupon(userId, cv.getCid(), cv.getId(), checkedGoodsList);
            cv.setAvailable(coupon != null);
        }
        return ResponseUtil.okList(couponVoList);
    }

    /**
     * 优惠券领取
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    public Object receive(String userId, String couponId) {
        LitemallCoupon coupon = couponService.findById(couponId);
        if(coupon == null){
            return ResponseUtil.badArgumentValue();
        }

        // 当前已领取数量和总数量比较
        Integer total = coupon.getTotal();
        Integer totalCoupons = couponUserService.countCoupon(couponId);
        if((total != 0) && (totalCoupons >= total)){
            return ResponseUtil.fail( "优惠券已领完");
        }

        // 当前用户已领取数量和用户限领数量比较
        Integer limit = coupon.getLimit().intValue();
        Integer userCounpons = couponUserService.countUserAndCoupon(userId, couponId);
        if((limit != 0) && (userCounpons >= limit)){
            return ResponseUtil.fail("优惠券已经领取过");
        }

        // 优惠券分发类型
        // 例如注册赠券类型的优惠券不能领取
        Short type = coupon.getType();
        if(type.equals(CouponType.TYPE_REGISTER.getStatus())){
            return ResponseUtil.fail( "新用户优惠券自动发送");
        }
        else if(type.equals(CouponType.TYPE_CODE.getStatus())){
            return ResponseUtil.fail( "优惠券只能兑换");
        }
        else if(!type.equals(CouponType.TYPE_COMMON.getStatus())){
            return ResponseUtil.fail( "优惠券类型不支持");
        }

        // 优惠券状态，已下架或者过期不能领取
        Short status = coupon.getStatus();
        if(status.equals(CouponStatus.STATUS_OUT.getStatus())){
            return ResponseUtil.fail("优惠券已领完");
        }
        else if(status.equals(CouponStatus.STATUS_EXPIRED.getStatus())){
            return ResponseUtil.fail("优惠券已经过期");
        }

        // 用户领券记录
        commonService.addCouponUser(userId, coupon, couponId);
        return ResponseUtil.ok();
    }

    /**
     * 优惠券兑换
     *
     * @param userId 用户ID
     * @param code   优惠券兑换码
     * @return 操作结果
     */
    public Object exchange(String userId, String code) {
        LitemallCoupon coupon = couponService.findByCode(code);
        if(coupon == null){
            return ResponseUtil.fail( "兑换码不正确");
        }
        String couponId = coupon.getId();
        // 当前已领取数量和总数量比较
        Integer total = coupon.getTotal();
        Integer totalCoupons = couponUserService.countCoupon(couponId);
        if((total != 0) && (totalCoupons >= total)){
            return ResponseUtil.fail( "优惠券已兑换");
        }

        // 当前用户已领取数量和用户限领数量比较
        Integer limit = coupon.getLimit().intValue();
        Integer userCounpons = couponUserService.countUserAndCoupon(userId, couponId);
        if((limit != 0) && (userCounpons >= limit)){
            return ResponseUtil.fail( "优惠券已兑换");
        }

        // 优惠券分发类型
        // 例如注册赠券类型的优惠券不能领取
        Short type = coupon.getType();
        if(type.equals(CouponType.TYPE_REGISTER.getStatus())){
            return ResponseUtil.fail( "新用户优惠券自动发送");
        }
        else if(type.equals(CouponType.TYPE_COMMON.getStatus())){
            return ResponseUtil.fail("优惠券只能领取，不能兑换");
        }
        else if(!type.equals(CouponType.TYPE_CODE.getStatus())){
            return ResponseUtil.fail("优惠券类型不支持");
        }

        // 优惠券状态，已下架或者过期不能领取
        Short status = coupon.getStatus();
        if(status.equals(CouponStatus.STATUS_OUT.getStatus())){
            return ResponseUtil.fail( "优惠券已兑换");
        }
        else if(status.equals(CouponStatus.STATUS_EXPIRED.getStatus())){
            return ResponseUtil.fail("优惠券已经过期");
        }
        // 用户领券记录
        commonService.addCouponUser(userId, coupon, couponId);
        return ResponseUtil.ok();
    }


}