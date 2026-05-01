package org.ysling.shopflow.admin.model.order.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 订单发货
 * @author Ysling
 */
@Data
public class OrderShipBody implements Serializable {

    /**
     * 订单ID
     */
    @NotValue(message = "订单ID不能为空")
    private String orderId;
    /**
     * 物流单号
     */
    @NotValue(message = "物流单号不能为空")
    private String shipSn;
    /**
     * 物流公司
     */
    @NotValue(message = "物流公司不能为空")
    private String shipChannel;

}
