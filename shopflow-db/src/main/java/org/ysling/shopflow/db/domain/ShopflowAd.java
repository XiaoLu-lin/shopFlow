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
 * 广告表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_ad")
public class ShopflowAd implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 广告表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 广告标题
     */
    @TableField("`name`")
    private String name;
    /**
     * 所广告的商品页面或者活动页面链接地址
     */
    @TableField("`link`")
    private String link;
    /**
     * 广告宣传图片
     */
    @TableField("`url`")
    private String url;
    /**
     * 广告位置：1则是首页
     */
    @TableField("`position`")
    private Byte position;
    /**
     * 活动内容
     */
    @TableField("`content`")
    private String content;
    /**
     * 广告开始时间
     */
    @TableField("`start_time`")
    private LocalDateTime startTime;
    /**
     * 广告结束时间
     */
    @TableField("`end_time`")
    private LocalDateTime endTime;
    /**
     * 是否启动
     */
    @TableField("`enabled`")
    private Boolean enabled;
    /**
     * 权重用于排序
     */
    @TableField("`weight`")
    private Integer weight;
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
     * 广告表ID
     */
    public static final String ID = "`id`";
    /**
     * 广告标题
     */
    public static final String NAME = "`name`";
    /**
     * 所广告的商品页面或者活动页面链接地址
     */
    public static final String LINK = "`link`";
    /**
     * 广告宣传图片
     */
    public static final String URL = "`url`";
    /**
     * 广告位置：1则是首页
     */
    public static final String POSITION = "`position`";
    /**
     * 活动内容
     */
    public static final String CONTENT = "`content`";
    /**
     * 广告开始时间
     */
    public static final String START_TIME = "`start_time`";
    /**
     * 广告结束时间
     */
    public static final String END_TIME = "`end_time`";
    /**
     * 是否启动
     */
    public static final String ENABLED = "`enabled`";
    /**
     * 权重用于排序
     */
    public static final String WEIGHT = "`weight`";
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
