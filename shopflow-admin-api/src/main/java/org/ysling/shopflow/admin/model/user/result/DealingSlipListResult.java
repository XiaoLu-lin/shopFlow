package org.ysling.shopflow.admin.model.user.result;


import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DealingSlipListResult implements Serializable {

    /**
     * 交易记录单ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 微信登录openid
     */
    private String openid;
    /**
     * 订单编号
     */
    private String orderSn;
    /**
     * 交易金额
     */
    private BigDecimal award;
    /**
     * 交易后余额
     */
    private BigDecimal balance;
    /**
     * 交易类型：0:订单，1:赏金，2:分享，3:系统设置
     */
    private Short dealType;
    /**
     * 交易备注
     */
    private String remark;
    /**
     * 转账状态
     */
    private String status;
    /**
     * 交易状态
     */
    private String statusText;
    /**
     * 交易类型：0:订单，1:赏金，2:分享，3:系统设置
     */
    private String dealTypeText;
    /**
     * 交易IP地址
     */
    private String lastDealIp;
    /**
     * 商家批次单号
     */
    private String outBatchNo;
    /**
     * 微信转账批次单号
     */
    private String batchId;
    /**
     * 转账时间
     */
    private LocalDateTime batchTime;
    /**
     * 创建时间
     */
    private LocalDateTime addTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
