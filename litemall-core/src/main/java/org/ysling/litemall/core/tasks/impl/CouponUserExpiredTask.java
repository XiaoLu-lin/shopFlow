package org.ysling.litemall.core.tasks.impl;
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
import org.ysling.litemall.core.tasks.service.TaskRunnable;
import org.ysling.litemall.core.utils.BeanUtil;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.enums.CouponUserStatus;
import org.ysling.litemall.db.service.ICouponUserService;

/**
 * 用户优惠券过期队列
 * @author Ysling
 */
@Slf4j
public class CouponUserExpiredTask extends TaskRunnable {

    /**用户优惠券ID*/
    private final String couponUserId;
    /**id前缀*/
    private static final String idPrefix = "CouponUserExpiredTask-";
    /**任务名称*/
    private static final String taskName = "用户优惠券过期";


    public CouponUserExpiredTask(LitemallCouponUser couponUser, long delayInMilliseconds){
        super(idPrefix + couponUser.getId(), delayInMilliseconds, couponUser.getTenantId() , taskName);
        this.couponUserId = couponUser.getId();
    }

    @Override
    public void runTask() {
        ICouponUserService couponUserService = BeanUtil.getBean(ICouponUserService.class);
        LitemallCouponUser couponUser = couponUserService.findById(this.couponUserId);
        if(couponUser == null || !CouponUserStatus.STATUS_USABLE.equals(couponUser.getStatus())) {
            return;
        }
        couponUser.setStatus(CouponUserStatus.STATUS_EXPIRED.getStatus());
        if (couponUserService.updateVersionSelective(couponUser) == 0) {
            throw new RuntimeException("用户优惠券过期设置失败");
        }
    }
}
