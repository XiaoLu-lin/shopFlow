package org.ysling.shopflow.wx.model.aftersale.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowAftersale;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;

import java.io.Serializable;
import java.util.List;

/**
 * 售后详情响应结果
 * @author Ysling
 */
@Data
public class AftersaleDetailResult implements Serializable {

    /**
     * 订单信息
     */
    private ShopflowOrder order;

    /**
     * 售后信息
     */
    private ShopflowAftersale aftersale;

    /**
     * 售后商品信息
     */
    private ShopflowOrderGoods orderGoods;



}
