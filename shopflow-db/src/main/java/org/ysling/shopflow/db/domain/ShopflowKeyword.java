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
 * 关键字表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_keyword")
public class ShopflowKeyword implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 关键字表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 关键字
     */
    @TableField("`keyword`")
    private String keyword;
    /**
     * 关键字的跳转链接
     */
    @TableField("`url`")
    private String url;
    /**
     * 是否是热门关键字
     */
    @TableField("`is_hot`")
    private Boolean isHot;
    /**
     * 是否是默认关键字
     */
    @TableField("`is_default`")
    private Boolean isDefault;
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
     * 关键字表ID
     */
    public static final String ID = "`id`";
    /**
     * 关键字
     */
    public static final String KEYWORD = "`keyword`";
    /**
     * 关键字的跳转链接
     */
    public static final String URL = "`url`";
    /**
     * 是否是热门关键字
     */
    public static final String IS_HOT = "`is_hot`";
    /**
     * 是否是默认关键字
     */
    public static final String IS_DEFAULT = "`is_default`";
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
