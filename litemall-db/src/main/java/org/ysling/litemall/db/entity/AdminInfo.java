package org.ysling.litemall.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class AdminInfo implements Serializable {

    /**
     * 管理员名称
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像图片
     */
    private String avatar;

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
     * 角色列表
     */
    private String[] roleIds;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 所属租户
     */
    private String tenant;


}
