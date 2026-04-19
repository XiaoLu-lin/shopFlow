package org.ysling.litemall.db.domain;

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
import org.ysling.litemall.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 用户分享记录表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_share")
public class LitemallShare implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 分享ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 订单ID
     */
    @TableField("`order_id`")
    private String orderId;
    /**
     * 分享者ID
     */
    @TableField("`inviter_id`")
    private String inviterId;
    /**
     * 奖励金额
     */
    @TableField("`award`")
    private BigDecimal award;
    /**
     * 赏金任务状态，0待加入，1加入成功，2取消加入
     */
    @TableField("`status`")
    private Short status;
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
     * 分享ID
     */
    public static final String ID = "`id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 订单ID
     */
    public static final String ORDER_ID = "`order_id`";
    /**
     * 分享者ID
     */
    public static final String INVITER_ID = "`inviter_id`";
    /**
     * 奖励金额
     */
    public static final String AWARD = "`award`";
    /**
     * 赏金任务状态，0待加入，1加入成功，2取消加入
     */
    public static final String STATUS = "`status`";
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
