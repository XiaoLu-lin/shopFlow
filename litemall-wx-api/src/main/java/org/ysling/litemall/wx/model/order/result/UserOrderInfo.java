package org.ysling.litemall.wx.model.order.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class UserOrderInfo implements Serializable {

    /**
     * 待支付
     */
    private Integer unpaid = 0;
    /**
     * 待发货
     */
    private Integer unship = 0;
    /**
     * 待收货
     */
    private Integer unrecv = 0;
    /**
     * 待评论
     */
    private Integer uncomment = 0;


}
