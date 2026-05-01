package org.ysling.shopflow.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowCouponUser;
import org.ysling.shopflow.db.service.impl.CouponUserServiceImpl;
import org.ysling.shopflow.wx.model.coupon.body.CouponListBody;
import java.util.List;

/**
 * 优惠券使用服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_coupon_user")
public class WxCouponUserService extends CouponUserServiceImpl {


    @Cacheable(sync = true)
    public Integer countUserAndCoupon(String userId, String couponId) {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        wrapper.eq(ShopflowCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(count(wrapper));
    }


    @Cacheable(sync = true)
    public Integer countCoupon(String couponId) {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.COUPON_ID , couponId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<ShopflowCouponUser> queryList(String userId, CouponListBody body) {
        QueryWrapper<ShopflowCouponUser> wrapper = startPage(body);
        if (userId != null) {
            wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        }
        if (body.getStatus() != null) {
            wrapper.eq(ShopflowCouponUser.STATUS , body.getStatus());
        }
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public List<ShopflowCouponUser> queryAll(String userId) {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        return queryAll(wrapper);
    }
    

}
