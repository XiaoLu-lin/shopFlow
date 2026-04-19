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
import org.ysling.litemall.db.service.ICouponService;
import org.ysling.litemall.db.service.ICouponUserService;
import org.ysling.litemall.db.service.IGoodsService;
import org.ysling.litemall.db.domain.LitemallCart;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.enums.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 优惠券检查
 * @author Ysling
 */
@Service
public class CouponVerifyService {

    @Autowired
    private ICouponUserService couponUserService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private CommonService commonService;

    /**
     * 检测优惠券是否适合
     */
    public LitemallCoupon checkCoupon(String userId, String couponId, String userCouponId, List<LitemallCart> cartList) {
        LitemallCoupon coupon = couponService.findById(couponId);
        if (coupon == null || coupon.getDeleted()) {
            return null;
        }

        LitemallCouponUser couponUser = couponUserService.findById(userCouponId);
        if (couponUser == null) {
            couponUser = commonService.queryOne(userId, couponId);
        } else if (!couponId.equals(couponUser.getCouponId())) {
            return null;
        }

        if (couponUser == null) {
            return null;
        }

        // 检查是否超期
        Short timeType = coupon.getTimeType();
        Short days = coupon.getDays();
        LocalDateTime now = LocalDateTime.now();
        if (timeType.equals(CouponTimeType.TIME_TYPE_TIME.getStatus())) {
            if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
                return null;
            }
        } else if(timeType.equals(CouponTimeType.TIME_TYPE_DAYS.getStatus())) {
            LocalDateTime expired = couponUser.getAddTime().plusDays(days);
            if (now.isAfter(expired)) {
                return null;
            }
        } else {
            return null;
        }

        // 检测商品是否符合
        Map<String, List<LitemallCart>> cartMap = new HashMap<>();
        //可使用优惠券的商品或分类
        List<String> goodsValueList = new ArrayList<>(Arrays.asList(coupon.getGoodsIds()));
        Short goodType = coupon.getGoodsType();

        // 商品总价
        BigDecimal checkedGoodsPrice = new BigDecimal("0.00");
        for (LitemallCart cart : cartList) {
            checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getNumber())));
        }

        // 判断商品是否满足优惠券限制
        if (goodType.equals(CouponGoodsType.GOODS_TYPE_CATEGORY.getStatus()) ||
                goodType.equals((CouponGoodsType.GOODS_TYPE_ARRAY.getStatus()))) {
            for (LitemallCart cart : cartList) {
                String key;
                if (goodType.equals(CouponGoodsType.GOODS_TYPE_ARRAY.getStatus())){
                    key = cart.getGoodsId();
                }else {
                    key = goodsService.findById(cart.getGoodsId()).getCategoryId();
                }

                List<LitemallCart> carts = cartMap.get(key);
                if (carts == null) {
                    carts = new LinkedList<>();
                }
                carts.add(cart);
                cartMap.put(key, carts);
            }
            //购物车中可以使用优惠券的商品或分类
            goodsValueList.retainAll(cartMap.keySet());
            //可使用优惠券的商品的总价格
            BigDecimal total = BigDecimal.valueOf(0);
            for (String goodsId : goodsValueList) {
                List<LitemallCart> carts = cartMap.get(goodsId);
                for (LitemallCart cart : carts) {
                    total = total.add(cart.getPrice().multiply(BigDecimal.valueOf(cart.getNumber())));
                }
            }
            //是否达到优惠券满减金额
            if (total.compareTo(coupon.getMin()) < 0) {
                return null;
            }
        }

        // 检测订单状态
        Short status = coupon.getStatus();
        if (!status.equals(CouponStatus.STATUS_NORMAL.getStatus())) {
            return null;
        }
        // 检测是否满足最低消费
        if (checkedGoodsPrice.compareTo(coupon.getMin()) < 0) {
            return null;
        }

        return coupon;
    }


}