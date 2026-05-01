package org.ysling.shopflow.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.shopflow.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 优惠券用户使用表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_coupon_user")
public class ShopflowCouponUser implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 优惠券使用表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 优惠券ID
     */
    @TableField("`coupon_id`")
    private String couponId;
    /**
     * 使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；
     */
    @TableField("`status`")
    private Short status;
    /**
     * 使用时间
     */
    @TableField("`used_time`")
    private LocalDateTime usedTime;
    /**
     * 有效期开始时间
     */
    @TableField("`start_time`")
    private LocalDateTime startTime;
    /**
     * 有效期截至时间
     */
    @TableField("`end_time`")
    private LocalDateTime endTime;
    /**
     * 订单ID
     */
    @TableField("`order_id`")
    private String orderId;
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
     * 优惠券使用表ID
     */
    public static final String ID = "`id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 优惠券ID
     */
    public static final String COUPON_ID = "`coupon_id`";
    /**
     * 使用状态, 如果是0则未使用；如果是1则已使用；如果是2则已过期；如果是3则已经下架；
     */
    public static final String STATUS = "`status`";
    /**
     * 使用时间
     */
    public static final String USED_TIME = "`used_time`";
    /**
     * 有效期开始时间
     */
    public static final String START_TIME = "`start_time`";
    /**
     * 有效期截至时间
     */
    public static final String END_TIME = "`end_time`";
    /**
     * 订单ID
     */
    public static final String ORDER_ID = "`order_id`";
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
