package org.ysling.litemall.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.service.impl.CouponUserServiceImpl;
import org.ysling.litemall.wx.model.coupon.body.CouponListBody;
import java.util.List;

/**
 * 优惠券使用服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_coupon_user")
public class WxCouponUserService extends CouponUserServiceImpl {


    @Cacheable(sync = true)
    public Integer countUserAndCoupon(String userId, String couponId) {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.USER_ID , userId);
        wrapper.eq(LitemallCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(count(wrapper));
    }


    @Cacheable(sync = true)
    public Integer countCoupon(String couponId) {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<LitemallCouponUser> queryList(String userId, CouponListBody body) {
        QueryWrapper<LitemallCouponUser> wrapper = startPage(body);
        if (userId != null) {
            wrapper.eq(LitemallCouponUser.USER_ID , userId);
        }
        if (body.getStatus() != null) {
            wrapper.eq(LitemallCouponUser.STATUS , body.getStatus());
        }
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public List<LitemallCouponUser> queryAll(String userId) {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.USER_ID , userId);
        return queryAll(wrapper);
    }
    

}
