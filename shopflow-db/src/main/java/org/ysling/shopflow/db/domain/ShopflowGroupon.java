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
 * 团购活动表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_groupon")
public class ShopflowGroupon implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 团购活动表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 关联的订单ID
     */
    @TableField("`order_id`")
    private String orderId;
    /**
     * 如果是开团用户，则groupon_id是0；如果是参团用户，则groupon_id是团购活动ID
     */
    @TableField("`groupon_id`")
    private String grouponId;
    /**
     * 团购规则ID，关联shopflow_groupon_rules表ID字段
     */
    @TableField("`rules_id`")
    private String rulesId;
    /**
     * 用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 团购分享图片地址
     */
    @TableField("`share_url`")
    private String shareUrl;
    /**
     * 开团用户ID
     */
    @TableField("`creator_user_id`")
    private String creatorUserId;
    /**
     * 团购活动状态，开团未支付则0，开团中则1，团购失败则2，团购成功则3，团购取消则4
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
     * 团购活动表ID
     */
    public static final String ID = "`id`";
    /**
     * 关联的订单ID
     */
    public static final String ORDER_ID = "`order_id`";
    /**
     * 如果是开团用户，则groupon_id是0；如果是参团用户，则groupon_id是团购活动ID
     */
    public static final String GROUPON_ID = "`groupon_id`";
    /**
     * 团购规则ID，关联shopflow_groupon_rules表ID字段
     */
    public static final String RULES_ID = "`rules_id`";
    /**
     * 用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 团购分享图片地址
     */
    public static final String SHARE_URL = "`share_url`";
    /**
     * 开团用户ID
     */
    public static final String CREATOR_USER_ID = "`creator_user_id`";
    /**
     * 团购活动状态，开团未支付则0，开团中则1，团购失败则2，团购成功则3，团购取消则4
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
