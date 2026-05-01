package org.ysling.shopflow.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.shopflow.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 用户交易记录
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_dealing_slip")
public class ShopflowDealingSlip implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 交易记录单ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 微信登录openid
     */
    @TableField("`openid`")
    private String openid;
    /**
     * 订单编号
     */
    @TableField("`order_sn`")
    private String orderSn;
    /**
     * 交易金额
     */
    @TableField("`award`")
    private BigDecimal award;
    /**
     * 交易后余额
     */
    @TableField("`balance`")
    private BigDecimal balance;
    /**
     * 交易类型：0:订单，1:赏金，2:分享，3:系统设置
     */
    @TableField("`deal_type`")
    private Short dealType;
    /**
     * 交易备注
     */
    @TableField("`remark`")
    private String remark;
    /**
     * 转账状态
     */
    @TableField("`status`")
    private String status;
    /**
     * 交易IP地址
     */
    @TableField("`last_deal_ip`")
    private String lastDealIp;
    /**
     * 商家批次单号
     */
    @TableField("`out_batch_no`")
    private String outBatchNo;
    /**
     * 微信转账批次单号
     */
    @TableField("`batch_id`")
    private String batchId;
    /**
     * 转账时间
     */
    @TableField("`batch_time`")
    private LocalDateTime batchTime;
    /**
     * 创建时间
     */
    @TableField(value = "`add_time`", fill = FieldFill.INSERT)
    private LocalDateTime addTime;
    /**
     * 更新时间
     */
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删除
     */
    @TableField("`deleted`")
    @TableLogic
    private Boolean deleted;
    /**
     * 租户ID，用于分割多个租户
     */
    @TableField("`tenant_id`")
    private String tenantId;
    /**
     * 更新版本号
     */
    @TableField("`version`")
    @Version
    private Integer version;

    /////////////////////////////////
    // 数据库字段常量
    ////////////////////////////////

    /**
     * 交易记录单ID
     */
    public static final String ID = "`id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 微信登录openid
     */
    public static final String OPENID = "`openid`";
    /**
     * 订单编号
     */
    public static final String ORDER_SN = "`order_sn`";
    /**
     * 交易金额
     */
    public static final String AWARD = "`award`";
    /**
     * 交易后余额
     */
    public static final String BALANCE = "`balance`";
    /**
     * 交易类型：0:订单，1:赏金，2:分享，3:系统设置
     */
    public static final String DEAL_TYPE = "`deal_type`";
    /**
     * 交易备注
     */
    public static final String REMARK = "`remark`";
    /**
     * 转账状态
     */
    public static final String STATUS = "`status`";
    /**
     * 交易IP地址
     */
    public static final String LAST_DEAL_IP = "`last_deal_ip`";
    /**
     * 商家批次单号
     */
    public static final String OUT_BATCH_NO = "`out_batch_no`";
    /**
     * 微信转账批次单号
     */
    public static final String BATCH_ID = "`batch_id`";
    /**
     * 转账时间
     */
    public static final String BATCH_TIME = "`batch_time`";
    /**
     * 创建时间
     */
    public static final String ADD_TIME = "`add_time`";
    /**
     * 更新时间
     */
    public static final String UPDATE_TIME = "`update_time`";
    /**
     * 逻辑删除
     */
    public static final String DELETED = "`deleted`";
    /**
     * 租户ID，用于分割多个租户
     */
    public static final String TENANT_ID = "`tenant_id`";
    /**
     * 更新版本号
     */
    public static final String VERSION = "`version`";
}
