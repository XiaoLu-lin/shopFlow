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
 * 赏金任务参与表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_reward")
public class ShopflowReward implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 赏金任务参与表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 赏金任务的ID
     */
    @TableField("`task_id`")
    private String taskId;
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
     * 如果是分享用户，则reward_id是0；如果是活动参与用户，则reward_id是活动ID
     */
    @TableField("`reward_id`")
    private String rewardId;
    /**
     * 奖励金额
     */
    @TableField("`award`")
    private BigDecimal award;
    /**
     * 赏金分享图片地址
     */
    @TableField("`share_url`")
    private String shareUrl;
    /**
     * 分享用户ID
     */
    @TableField("`creator_user_id`")
    private String creatorUserId;
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
     * 赏金任务参与表ID
     */
    public static final String ID = "`id`";
    /**
     * 赏金任务的ID
     */
    public static final String TASK_ID = "`task_id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 订单ID
     */
    public static final String ORDER_ID = "`order_id`";
    /**
     * 如果是分享用户，则reward_id是0；如果是活动参与用户，则reward_id是活动ID
     */
    public static final String REWARD_ID = "`reward_id`";
    /**
     * 奖励金额
     */
    public static final String AWARD = "`award`";
    /**
     * 赏金分享图片地址
     */
    public static final String SHARE_URL = "`share_url`";
    /**
     * 分享用户ID
     */
    public static final String CREATOR_USER_ID = "`creator_user_id`";
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
