package org.ysling.litemall.wx.model.order.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallGroupon;
import org.ysling.litemall.db.domain.LitemallGrouponRules;
import org.ysling.litemall.db.domain.LitemallOrderGoods;
import org.ysling.litemall.db.entity.UserInfo;

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
    private LitemallOrderGoods orderGoods;
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
    private LitemallGroupon groupon;
    /**
     * 团购规则
     */
    private LitemallGrouponRules rules;
    /**
     * 用户信息列表
     */
    private List<UserInfo> joiners;

}
