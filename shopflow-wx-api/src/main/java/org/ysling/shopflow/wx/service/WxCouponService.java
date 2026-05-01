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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowCoupon;
import org.ysling.shopflow.db.domain.ShopflowCouponUser;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.enums.CouponStatus;
import org.ysling.shopflow.db.enums.CouponType;
import org.ysling.shopflow.db.service.ICouponUserService;
import org.ysling.shopflow.db.service.impl.CouponServiceImpl;
import org.ysling.shopflow.wx.model.coupon.result.CouponResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 优惠券服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_coupon")
public class WxCouponService extends CouponServiceImpl {

    @Autowired
    private ICouponUserService couponUserService;


    public List<CouponResult> change(List<ShopflowCouponUser> couponList) {
        List<CouponResult> couponVoList = new ArrayList<>(couponList.size());
        for(ShopflowCouponUser couponUser : couponList){
            String couponId = couponUser.getCouponId();
            ShopflowCoupon coupon = findById(couponId);
            CouponResult couponVo = new CouponResult();
            couponVo.setId(couponUser.getId());
            couponVo.setCid(coupon.getId());
            couponVo.setName(coupon.getName());
            couponVo.setDesc(coupon.getDepict());
            couponVo.setTag(coupon.getTag());
            couponVo.setMin(coupon.getMin());
            couponVo.setDiscount(coupon.getDiscount());
            couponVo.setStartTime(couponUser.getStartTime());
            couponVo.setEndTime(couponUser.getEndTime());
            couponVoList.add(couponVo);
        }
        return couponVoList;
    }

    /**
     * 查询
     */
    @Cacheable(sync = true)
    public List<ShopflowCoupon> queryList(PageBody body) {
        QueryWrapper<ShopflowCoupon> wrapper = startPage(body);
        wrapper.eq(ShopflowCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        wrapper.eq(ShopflowCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowCoupon> queryAvailableList(String userId, Integer limit) {
        //分页
        PageBody body = new PageBody(limit);
        if (userId == null){
            return queryList(body);
        }

        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.USER_ID , userId);
        List<ShopflowCouponUser> userList = couponUserService.queryAll(wrapper);

        //查询优惠券
        QueryWrapper<ShopflowCoupon> startPage = startPage(body);
        // 过滤掉登录账号已经领取过的coupon
        if(userList != null && !userList.isEmpty()){
            startPage.notIn(ShopflowCoupon.ID , userList.stream().map(ShopflowCouponUser::getCouponId).collect(Collectors.toList()));
        }
        //查询条件
        startPage.eq(ShopflowCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        startPage.eq(ShopflowCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return queryAll(startPage);
    }


    @Cacheable(sync = true)
    public ShopflowCoupon findByCode(String code) {
        QueryWrapper<ShopflowCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCoupon.CODE , code);
        wrapper.eq(ShopflowCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        wrapper.eq(ShopflowCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        List<ShopflowCoupon> couponList =  queryAll(wrapper);
        if(couponList.size() > 1){
            return null;
        } else if(couponList.size() == 0){
            return null;
        } else {
            return couponList.get(0);
        }
    }

}
