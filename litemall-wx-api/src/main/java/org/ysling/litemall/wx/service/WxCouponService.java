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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.CouponStatus;
import org.ysling.litemall.db.enums.CouponType;
import org.ysling.litemall.db.service.ICouponUserService;
import org.ysling.litemall.db.service.impl.CouponServiceImpl;
import org.ysling.litemall.wx.model.coupon.result.CouponResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 优惠券服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_coupon")
public class WxCouponService extends CouponServiceImpl {

    @Autowired
    private ICouponUserService couponUserService;


    public List<CouponResult> change(List<LitemallCouponUser> couponList) {
        List<CouponResult> couponVoList = new ArrayList<>(couponList.size());
        for(LitemallCouponUser couponUser : couponList){
            String couponId = couponUser.getCouponId();
            LitemallCoupon coupon = findById(couponId);
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
    public List<LitemallCoupon> queryList(PageBody body) {
        QueryWrapper<LitemallCoupon> wrapper = startPage(body);
        wrapper.eq(LitemallCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        wrapper.eq(LitemallCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallCoupon> queryAvailableList(String userId, Integer limit) {
        //分页
        PageBody body = new PageBody(limit);
        if (userId == null){
            return queryList(body);
        }

        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.USER_ID , userId);
        List<LitemallCouponUser> userList = couponUserService.queryAll(wrapper);

        //查询优惠券
        QueryWrapper<LitemallCoupon> startPage = startPage(body);
        // 过滤掉登录账号已经领取过的coupon
        if(userList != null && !userList.isEmpty()){
            startPage.notIn(LitemallCoupon.ID , userList.stream().map(LitemallCouponUser::getCouponId).collect(Collectors.toList()));
        }
        //查询条件
        startPage.eq(LitemallCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        startPage.eq(LitemallCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        return queryAll(startPage);
    }


    @Cacheable(sync = true)
    public LitemallCoupon findByCode(String code) {
        QueryWrapper<LitemallCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCoupon.CODE , code);
        wrapper.eq(LitemallCoupon.TYPE , CouponType.TYPE_COMMON.getStatus());
        wrapper.eq(LitemallCoupon.STATUS , CouponStatus.STATUS_NORMAL.getStatus());
        List<LitemallCoupon> couponList =  queryAll(wrapper);
        if(couponList.size() > 1){
            return null;
        } else if(couponList.size() == 0){
            return null;
        } else {
            return couponList.get(0);
        }
    }

}
