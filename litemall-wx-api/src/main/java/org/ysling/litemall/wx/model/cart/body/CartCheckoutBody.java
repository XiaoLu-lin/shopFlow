package org.ysling.litemall.wx.model.cart.body;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class CartCheckoutBody implements Serializable {

    /**
     * 购物车商品ID：
     * 如果购物车商品ID是空，则下单当前用户所有购物车商品；
     * 如果购物车商品ID非空，则只下单当前购物车商品。
     */
    private String cartId;

    /**
     * 收货地址ID：
     * 如果收货地址ID是空，则查询当前用户的默认地址。
     */
    private String addressId;

    /**
     * 优惠券ID：
     * 如果优惠券ID是空，则自动选择合适的优惠券。
     */
    private String couponId;

    /**
     * 是否使用余额抵扣订单金额
     */
    private Boolean isDeduction;

    /**
     * 用户选择的优惠券ID
     */
    private String userCouponId;

    /**
     * 团购规则ID
     */
    private String grouponRulesId;

}
