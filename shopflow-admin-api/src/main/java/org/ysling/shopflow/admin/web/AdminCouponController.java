package org.ysling.shopflow.admin.web;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.coupon.body.CouponListBody;
import org.ysling.shopflow.admin.model.coupon.body.CouponUserListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowCoupon;
import org.ysling.shopflow.db.enums.CouponType;
import org.ysling.shopflow.admin.service.AdminCouponService;
import org.ysling.shopflow.admin.service.AdminCouponUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;


/**
 * 优惠券管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/coupon")
@Validated
public class AdminCouponController {

    @Autowired
    private AdminCouponService couponService;
    @Autowired
    private AdminCouponUserService couponUserService;


    /**
     * 查询
     */
    @SaCheckPermission("admin:coupon:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(CouponListBody body) {
        return ResponseUtil.okList(couponService.querySelective(body));
    }


    /**
     * 查询用户
     */
    @SaCheckPermission("admin:coupon:join")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "查询用户")
    @GetMapping("/join")
    public Object listUser(CouponUserListBody body) {
        return ResponseUtil.okList(couponUserService.querySelective(body));
    }

    /**
     * 详情
     */
    @SaCheckPermission("admin:coupon:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(couponService.findById(id));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:coupon:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowCoupon coupon) {
        Object error = couponService.validate(coupon);
        if (error != null) {
            return error;
        }
        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponType.TYPE_CODE.getStatus())) {
            coupon.setCode(couponService.generateCode());
        }
        if (couponService.add(coupon) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:coupon:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowCoupon coupon) {
        Object error = couponService.validate(coupon);
        if (error != null) {
            return error;
        }
        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponType.TYPE_CODE.getStatus())) {
            coupon.setCode(couponService.generateCode());
        }
        if (couponService.updateVersionSelective(coupon) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:coupon:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "优惠券管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (couponService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
