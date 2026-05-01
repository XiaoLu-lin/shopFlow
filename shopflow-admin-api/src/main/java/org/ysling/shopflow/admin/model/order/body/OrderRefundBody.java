package org.ysling.shopflow.admin.model.order.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 订单退款
 * @author Ysling
 */
@Data
public class OrderRefundBody implements Serializable {

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
