package org.ysling.litemall.admin.service;
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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.coupon.body.CouponListBody;
import org.ysling.litemall.core.utils.RandomStrUtil;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.ysling.litemall.db.enums.CouponStatus;
import org.ysling.litemall.db.enums.CouponType;
import org.ysling.litemall.db.service.impl.CouponServiceImpl;
import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_coupon")
public class AdminCouponService extends CouponServiceImpl {


    public Object validate(LitemallCoupon coupon) {
        String name = coupon.getName();
        String depict = coupon.getDepict();
        String tag = coupon.getTag();
        BigDecimal price = coupon.getDiscount();
        if (!ObjectUtils.allNotNull(name, depict, tag, price)) {
            return ResponseUtil.badArgument();
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    /**
     * 生成优惠码
     *
     * @return 可使用优惠码
     */
    public String generateCode() {
        String code = RandomStrUtil.getRandom(8, RandomStrUtil.TYPE.CAPITAL_NUMBER);
        while(findByCode(code) != null){
            code = RandomStrUtil.getRandom(8, RandomStrUtil.TYPE.CAPITAL_NUMBER);
        }
        return code;
    }


    @Cacheable(sync = true)
    public LitemallCoupon findByCode(String code) {
        QueryWrapper<LitemallCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCoupon.CODE , code);
        wrapper.eq(LitemallCoupon.TYPE , CouponType.TYPE_CODE.getStatus());
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
    
    @Cacheable(sync = true)
    public List<LitemallCoupon> querySelective(CouponListBody body) {
        QueryWrapper<LitemallCoupon> wrapper = startPage(body);
        if (body.getType() != null) {
            wrapper.eq(LitemallCoupon.TYPE , body.getType());
        }
        if (body.getStatus() != null) {
            wrapper.eq(LitemallCoupon.STATUS , body.getStatus());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallCoupon.NAME , body.getName());
        }
        return queryAll(wrapper);
    }



}
