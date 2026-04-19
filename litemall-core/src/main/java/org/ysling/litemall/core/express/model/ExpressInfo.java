package org.ysling.litemall.core.express.model;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 物流详情
 * @author Ysling
 */
@Data
public class ExpressInfo implements Serializable {

    /**用户ID*/
    @JsonProperty("EBusinessID")
    private String eBusinessID;

    /**订单编号*/
    @JsonProperty("OrderCode")
    private String orderCode;

    /**快递公司编码*/
    @JsonProperty("ShipperCode")
    private String shipperCode;

    /**物流公司名称*/
    private String shipperName;

    /**物流运单号*/
    @JsonProperty("LogisticCode")
    private String logisticCode;

    /**成功与否*/
    @JsonProperty("Success")
    private boolean success;

    /**失败原因*/
    @JsonProperty("Reason")
    private String reason;

    /**物流状态：2-在途中,3-签收,4-问题件*/
    @JsonProperty("State")
    private String state;

    /**用户ID*/
    @JsonProperty("Traces")
    private List<Traces> traces;

}