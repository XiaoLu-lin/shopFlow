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

import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 分发优惠券
 * @author Ysling
 */
@Service
public class CouponAssignService {


    @Autowired
    private CommonService commonService;


    /**
     * 分发注册优惠券
     */
    public void assignForRegister(String userId) {
        List<LitemallCoupon> couponList = commonService.queryRegister();
        for(LitemallCoupon coupon : couponList){
            String couponId = coupon.getId();
            Integer count = commonService.countUserAndCoupon(userId, couponId);
            if (count > 0) {
                continue;
            }

            Short limit = coupon.getLimit();
            while(limit > 0){
                commonService.addCouponUser(userId, coupon, couponId);
                limit--;
            }
        }
    }




}