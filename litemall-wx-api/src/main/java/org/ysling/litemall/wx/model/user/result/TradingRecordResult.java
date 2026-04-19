package org.ysling.litemall.wx.model.user.result;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class TradingRecordResult implements Serializable {

    /**
     * 交易金额
     */
    private BigDecimal award;
    /**
     * 交易状态
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
     * 转账时间
     */
    private LocalDateTime batchTime;
    /**
     * 图片
     */
    private String picUrl;

}
