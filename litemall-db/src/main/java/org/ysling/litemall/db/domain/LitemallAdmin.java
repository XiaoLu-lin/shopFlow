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
 * 管理员表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_admin")
public class LitemallAdmin implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 管理员表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 管理员名称
     */
    @TableField("`username`")
    private String username;
    /**
     * 管理员密码
     */
    @TableField("`password`")
    private String password;
    /**
     * 最近一次登录IP地址
     */
    @TableField("`last_login_ip`")
    private String lastLoginIp;
    /**
     * 最近一次登录时间
     */
    @TableField("`last_login_time`")
    private LocalDateTime lastLoginTime;
    /**
     * 微信openid
     */
    @TableField("`openid`")
    private String openid;
    /**
     * 用户手机号码
     */
    @TableField("`mobile`")
    private String mobile;
    /**
     * 管理员邮箱
     */
    @TableField("`mail`")
    private String mail;
    /**
     * 性别：0 未知， 1男， 2 女
     */
    @TableField("`gender`")
    private Byte gender;
    /**
     * 头像图片
     */
    @TableField("`avatar`")
    private String avatar;
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
     * 角色列表
     */
    @TableField(value = "`role_ids`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] roleIds;
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
     * 管理员表ID
     */
    public static final String ID = "`id`";
    /**
     * 管理员名称
     */
    public static final String USERNAME = "`username`";
    /**
     * 管理员密码
     */
    public static final String PASSWORD = "`password`";
    /**
     * 最近一次登录IP地址
     */
    public static final String LAST_LOGIN_IP = "`last_login_ip`";
    /**
     * 最近一次登录时间
     */
    public static final String LAST_LOGIN_TIME = "`last_login_time`";
    /**
     * 微信openid
     */
    public static final String OPENID = "`openid`";
    /**
     * 用户手机号码
     */
    public static final String MOBILE = "`mobile`";
    /**
     * 管理员邮箱
     */
    public static final String MAIL = "`mail`";
    /**
     * 性别：0 未知， 1男， 2 女
     */
    public static final String GENDER = "`gender`";
    /**
     * 头像图片
     */
    public static final String AVATAR = "`avatar`";
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
     * 角色列表
     */
    public static final String ROLE_IDS = "`role_ids`";
    /**
     * 租户ID，用于分割多个租户
     */
    public static final String TENANT_ID = "`tenant_id`";
    /**
     * 更新版本号
     */
    public static final String VERSION = "`version`";
}
