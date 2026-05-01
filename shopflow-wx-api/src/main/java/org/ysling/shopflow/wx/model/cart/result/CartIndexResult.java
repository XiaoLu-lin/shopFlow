package org.ysling.shopflow.wx.model.cart.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowCart;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class CartIndexResult implements Serializable {

    /**
     * 计算购物车信息
     */
    private CartTotalResult cartTotal;
    /**
     * 购物车商品
     */
    private List<ShopflowCart> cartList;

}
