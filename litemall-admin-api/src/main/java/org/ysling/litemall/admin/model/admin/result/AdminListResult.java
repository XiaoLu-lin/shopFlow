package org.ysling.litemall.admin.model.admin.result;



import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminListResult implements Serializable {

    /**
     * 管理员表ID
     */
    private String id;
    /**
     * 管理员名称
     */
    private String username;
    /**
     * 微信openid
     */
    private String openid;
    /**
     * 用户手机号码
     */
    private String mobile;
    /**
     * 管理员邮箱
     */
    private String mail;
    /**
     * 性别：0 未知， 1男， 2 女
     */
    private Byte gender;
    /**
     * 头像图片
     */
    private String avatar;
    /**
     * 角色列表
     */
    private String[] roleIds;
    /**
     * 最近一次登录IP地址
     */
    private String lastLoginIp;
    /**
     * 最近一次登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 是否在线
     */
    private Boolean checkLogin;
    /**
     * 登录token
     */
    private String loginToken;

    /**
     * 租户ID，用于分割多个租户
     */
    private String tenantId;

    /**
     * 更新版本号
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
