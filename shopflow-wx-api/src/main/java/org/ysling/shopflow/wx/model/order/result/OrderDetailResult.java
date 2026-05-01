package org.ysling.shopflow.wx.model.order.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowGroupon;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;
import org.ysling.shopflow.db.entity.UserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class OrderDetailResult implements Serializable {

    /**
     * 自提地址
     */
    private String address;
    /**
     * 是否自提
     */
    private Boolean isTakeTheir;
    /**
     * 订单信息
     */
    private OrderInfo orderInfo;
    /**
     * 订单提示
     */
    private Integer orderBasics;
    /**
     * 订单商品信息
     */
    private ShopflowOrderGoods orderGoods;
    /**
     * 团购提示
     */
    private Short grouponBasics;
    /**
     * 团购ID
     */
    private String linkGrouponId;
    /**
     * 用户信息
     */
    private UserInfo creator;
    /**
     * 团购信息
     */
    private ShopflowGroupon groupon;
    /**
     * 团购规则
     */
    private ShopflowGrouponRules rules;
    /**
     * 用户信息列表
     */
    private List<UserInfo> joiners;

}
