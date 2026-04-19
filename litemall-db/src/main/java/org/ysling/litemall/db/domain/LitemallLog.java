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
 * 操作日志表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_log")
public class LitemallLog implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 操作日志表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 管理员名称
     */
    @TableField("`admin`")
    private String admin;
    /**
     * 管理员地址
     */
    @TableField("`ip`")
    private String ip;
    /**
     * 操作分类
     */
    @TableField("`type`")
    private Short type;
    /**
     * 操作动作
     */
    @TableField("`action`")
    private String action;
    /**
     * 操作状态
     */
    @TableField("`status`")
    private Boolean status;
    /**
     * 操作结果，或者成功消息，或者失败消息
     */
    @TableField("`result`")
    private String result;
    /**
     * 补充信息
     */
    @TableField("`comment`")
    private String comment;
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
     * 操作日志表ID
     */
    public static final String ID = "`id`";
    /**
     * 管理员名称
     */
    public static final String ADMIN = "`admin`";
    /**
     * 管理员地址
     */
    public static final String IP = "`ip`";
    /**
     * 操作分类
     */
    public static final String TYPE = "`type`";
    /**
     * 操作动作
     */
    public static final String ACTION = "`action`";
    /**
     * 操作状态
     */
    public static final String STATUS = "`status`";
    /**
     * 操作结果，或者成功消息，或者失败消息
     */
    public static final String RESULT = "`result`";
    /**
     * 补充信息
     */
    public static final String COMMENT = "`comment`";
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
