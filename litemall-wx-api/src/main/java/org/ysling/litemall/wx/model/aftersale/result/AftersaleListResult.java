package org.ysling.litemall.wx.model.aftersale.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.domain.LitemallOrderGoods;

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
    private LitemallAftersale aftersale;

    /**
     * 售后商品信息
     */
    private LitemallOrderGoods orderGoods;



}
