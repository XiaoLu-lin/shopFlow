package org.ysling.litemall.wx.model.aftersale.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallOrderGoods;

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
    private LitemallOrder order;

    /**
     * 售后信息
     */
    private LitemallAftersale aftersale;

    /**
     * 售后商品信息
     */
    private LitemallOrderGoods orderGoods;



}
