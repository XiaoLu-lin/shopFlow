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
 * 售后表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_aftersale")
public class ShopflowAftersale implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 售后表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 售后编号
     */
    @TableField("`aftersale_sn`")
    private String aftersaleSn;
    /**
     * 订单ID
     */
    @TableField("`order_id`")
    private String orderId;
    /**
     * 用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 售后类型，0是未收货退款，1是已收货（无需退货）退款，2用户退货退款
     */
    @TableField("`type`")
    private Short type;
    /**
     * 退款原因
     */
    @TableField("`reason`")
    private String reason;
    /**
     * 退款金额
     */
    @TableField("`amount`")
    private BigDecimal amount;
    /**
     * 退款凭证图片链接数组
     */
    @TableField(value = "`pictures`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] pictures;
    /**
     * 退款说明
     */
    @TableField("`comment`")
    private String comment;
    /**
     * 售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消
     */
    @TableField("`status`")
    private Short status;
    /**
     * 管理员操作时间
     */
    @TableField("`handle_time`")
    private LocalDateTime handleTime;
    /**
     * 添加时间
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
     * 售后表ID
     */
    public static final String ID = "`id`";
    /**
     * 售后编号
     */
    public static final String AFTERSALE_SN = "`aftersale_sn`";
    /**
     * 订单ID
     */
    public static final String ORDER_ID = "`order_id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 售后类型，0是未收货退款，1是已收货（无需退货）退款，2用户退货退款
     */
    public static final String TYPE = "`type`";
    /**
     * 退款原因
     */
    public static final String REASON = "`reason`";
    /**
     * 退款金额
     */
    public static final String AMOUNT = "`amount`";
    /**
     * 退款凭证图片链接数组
     */
    public static final String PICTURES = "`pictures`";
    /**
     * 退款说明
     */
    public static final String COMMENT = "`comment`";
    /**
     * 售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消
     */
    public static final String STATUS = "`status`";
    /**
     * 管理员操作时间
     */
    public static final String HANDLE_TIME = "`handle_time`";
    /**
     * 添加时间
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
