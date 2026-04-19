package org.ysling.litemall.wx.model.order.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Data
public class OrderGoodsResult implements Serializable {

    /**
     * 订单商品ID
     */
    private String id;
    /**
     * 订单商品名称
     */
    private String goodsName;
    /**
     * 自提地址
     */
    private String address;
    /**
     * 是否自提
     */
    private Boolean isTakeTheir;
    /**
     * 购买数量
     */
    private Integer number;
    /**
     * 订单商品图片
     */
    private String picUrl;
    /**
     * 订单商品规格
     */
    private String[] specifications;
    /**
     * 订单商品价格
     */
    private BigDecimal price;

}
