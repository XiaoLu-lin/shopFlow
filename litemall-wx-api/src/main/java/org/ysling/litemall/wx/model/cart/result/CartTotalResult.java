package org.ysling.litemall.wx.model.cart.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Data
public class CartTotalResult implements Serializable {

    /**
     * 商品数量
     */
    private Integer goodsCount = 0;
    /**
     * 选中商品数量
     */
    private Integer checkedGoodsCount = 0;
    /**
     * 购物车商品总价
     */
    private BigDecimal goodsAmount = new BigDecimal("0.00");
    /**
     * 选中商品总价
     */
    private BigDecimal checkedGoodsAmount = new BigDecimal("0.00");

}
