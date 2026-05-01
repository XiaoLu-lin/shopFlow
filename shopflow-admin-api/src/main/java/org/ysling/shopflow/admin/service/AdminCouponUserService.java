package org.ysling.shopflow.admin.service;
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
import org.ysling.shopflow.admin.model.coupon.body.CouponUserListBody;
import org.ysling.shopflow.db.domain.ShopflowCouponUser;
import org.ysling.shopflow.db.service.impl.CouponUserServiceImpl;
import java.util.List;

/**
 * 优惠券使用服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_coupon_user")
public class AdminCouponUserService extends CouponUserServiceImpl {

    
    @Cacheable(sync = true)
    public List<ShopflowCouponUser> querySelective(CouponUserListBody body) {
        QueryWrapper<ShopflowCouponUser> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(ShopflowCouponUser.USER_ID , body.getUserId());
        }
        if(body.getCouponId() != null){
            wrapper.eq(ShopflowCouponUser.COUPON_ID , body.getCouponId());
        }
        if (body.getStatus() != null) {
            wrapper.eq(ShopflowCouponUser.STATUS , body.getStatus());
        }
        return queryAll(wrapper);
    }

}
