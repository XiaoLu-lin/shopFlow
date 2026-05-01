package org.ysling.shopflow.wx.model.aftersale.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowAftersale;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;

import java.io.Serializable;
import java.util.List;

/**
 * 售后列表响应结果
 * @author Ysling
 */
@Data
public class AftersaleListResult implements Serializable {

    /**
     * 售后状态文本
     */
    private String statusText;

    /**
     * 售后信息
     */
    private ShopflowAftersale aftersale;

    /**
     * 售后商品信息
     */
    private ShopflowOrderGoods orderGoods;



}
