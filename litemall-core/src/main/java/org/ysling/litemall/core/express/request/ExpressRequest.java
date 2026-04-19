package org.ysling.litemall.core.express.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 快递鸟请求参数
 * @author Ysling
 */
@Data
public class ExpressRequest implements Serializable {

    @JsonProperty("CustomerName")
    private String customerName = "";

    @JsonProperty("OrderCode")
    private String orderCode = "";

    @JsonProperty("ShipperCode")
    private String shipperCode = "";

    @JsonProperty("LogisticCode")
    private String logisticCode = "";

}
