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
 * 用户表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_user")
public class ShopflowUser implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 邀请者
     */
    @TableField("`inviter`")
    private String inviter;
    /**
     * 用户名称
     */
    @TableField("`username`")
    private String username;
    /**
     * 用户密码
     */
    @TableField("`password`")
    private String password;
    /**
     * 真实姓名
     */
    @TableField("`true_name`")
    private String trueName;
    /**
     * 性别：0 未知， 1男， 2女
     */
    @TableField("`gender`")
    private Byte gender;
    /**
     * 生日
     */
    @TableField("`birthday`")
    private LocalDateTime birthday;
    /**
     * 个人分享图片
     */
    @TableField("`share_url`")
    private String shareUrl;
    /**
     * 最近一次登录时间
     */
    @TableField("`last_login_time`")
    private LocalDateTime lastLoginTime;
    /**
     * 最近一次登录IP地址
     */
    @TableField("`last_login_ip`")
    private String lastLoginIp;
    /**
     * 0 普通用户，1 VIP用户，2 高级VIP用户
     */
    @TableField("`user_level`")
    private Byte userLevel;
    /**
     * 用户积分
     */
    @TableField("`integral`")
    private BigDecimal integral;
    /**
     * 用户昵称或网络名称
     */
    @TableField("`nick_name`")
    private String nickName;
    /**
     * 用户手机号码
     */
    @TableField("`mobile`")
    private String mobile;
    /**
     * 用户头像图片
     */
    @TableField("`avatar_url`")
    private String avatarUrl;
    /**
     * 微信登录openid
     */
    @TableField("`openid`")
    private String openid;
    /**
     * 微信登录会话KEY
     */
    @TableField("`session_key`")
    private String sessionKey;
    /**
     * 0 可用, 1 禁用, 2 注销
     */
    @TableField("`status`")
    private Byte status;
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
     * 用户表ID
     */
    public static final String ID = "`id`";
    /**
     * 邀请者
     */
    public static final String INVITER = "`inviter`";
    /**
     * 用户名称
     */
    public static final String USERNAME = "`username`";
    /**
     * 用户密码
     */
    public static final String PASSWORD = "`password`";
    /**
     * 真实姓名
     */
    public static final String TRUE_NAME = "`true_name`";
    /**
     * 性别：0 未知， 1男， 2女
     */
    public static final String GENDER = "`gender`";
    /**
     * 生日
     */
    public static final String BIRTHDAY = "`birthday`";
    /**
     * 个人分享图片
     */
    public static final String SHARE_URL = "`share_url`";
    /**
     * 最近一次登录时间
     */
    public static final String LAST_LOGIN_TIME = "`last_login_time`";
    /**
     * 最近一次登录IP地址
     */
    public static final String LAST_LOGIN_IP = "`last_login_ip`";
    /**
     * 0 普通用户，1 VIP用户，2 高级VIP用户
     */
    public static final String USER_LEVEL = "`user_level`";
    /**
     * 用户积分
     */
    public static final String INTEGRAL = "`integral`";
    /**
     * 用户昵称或网络名称
     */
    public static final String NICK_NAME = "`nick_name`";
    /**
     * 用户手机号码
     */
    public static final String MOBILE = "`mobile`";
    /**
     * 用户头像图片
     */
    public static final String AVATAR_URL = "`avatar_url`";
    /**
     * 微信登录openid
     */
    public static final String OPENID = "`openid`";
    /**
     * 微信登录会话KEY
     */
    public static final String SESSION_KEY = "`session_key`";
    /**
     * 0 可用, 1 禁用, 2 注销
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
