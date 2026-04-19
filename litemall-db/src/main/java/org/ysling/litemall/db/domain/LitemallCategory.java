package org.ysling.litemall.db.domain;

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
import org.ysling.litemall.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 类目表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_category")
public class LitemallCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 类目表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 类目名称
     */
    @TableField("`name`")
    private String name;
    /**
     * 类目关键字，以JSON数组格式
     */
    @TableField("`keywords`")
    private String keywords;
    /**
     * 类目广告语介绍
     */
    @TableField("`depict`")
    private String depict;
    /**
     * 父类目ID
     */
    @TableField("`pid`")
    private String pid;
    /**
     * 类目图标
     */
    @TableField("`icon_url`")
    private String iconUrl;
    /**
     * 类目图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 目录等级
     */
    @TableField("`level`")
    private String level;
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
     * 类目表ID
     */
    public static final String ID = "`id`";
    /**
     * 类目名称
     */
    public static final String NAME = "`name`";
    /**
     * 类目关键字，以JSON数组格式
     */
    public static final String KEYWORDS = "`keywords`";
    /**
     * 类目广告语介绍
     */
    public static final String DEPICT = "`depict`";
    /**
     * 父类目ID
     */
    public static final String PID = "`pid`";
    /**
     * 类目图标
     */
    public static final String ICON_URL = "`icon_url`";
    /**
     * 类目图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 目录等级
     */
    public static final String LEVEL = "`level`";
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
