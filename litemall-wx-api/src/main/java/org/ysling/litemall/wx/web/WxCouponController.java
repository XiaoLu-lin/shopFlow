package org.ysling.litemall.wx.web;
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
import org.ysling.litemall.core.annotation.JsonBody;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.wx.model.coupon.body.CouponListBody;
import org.ysling.litemall.wx.web.impl.WxWebCouponService;

/**
 * 优惠券服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/coupon")
@Validated
public class WxCouponController {

    @Autowired
    private WxWebCouponService couponService;

    /**
     * 优惠券列表
     */
    @GetMapping("list")
    public Object list(PageBody body) {
        return couponService.list(body);
    }

    /**
     * 个人优惠券列表
     */
    @GetMapping("user")
    public Object user(@LoginUser String userId, CouponListBody body) {
        return couponService.user(userId , body);
    }

    /**
     * 当前购物车下单商品订单可用优惠券
     */
    @GetMapping("select")
    public Object selectList(@LoginUser String userId, String cartId) {
        return couponService.selectList(userId , cartId);
    }

    /**
     * 优惠券领取
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    @PostMapping("receive")
    public Object receive(@LoginUser String userId, @JsonBody String couponId) {
        return couponService.receive(userId , couponId);
    }

    /**
     * 优惠券兑换
     * @param userId 用户ID
     * @param code   优惠券兑换码
     * @return 操作结果
     */
    @PostMapping("exchange")
    public Object exchange(@LoginUser String userId, @JsonBody String code) {
        return couponService.receive(userId , code);
    }


}