package org.ysling.litemall.wx.model.cart.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallCart;

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
    private List<LitemallCart> cartList;

}
