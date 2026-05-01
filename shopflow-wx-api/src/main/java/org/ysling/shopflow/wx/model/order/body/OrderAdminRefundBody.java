package org.ysling.shopflow.wx.model.order.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class OrderAdminRefundBody implements Serializable {

    /**
     * 订单ID
     */
    @NotValue(message = "订单ID不能为空")
    private String orderId;

    /**
     * 退款金额
     */
    @NotValue(message = "退款金额不能为空")
    private String refundMoney;

}
