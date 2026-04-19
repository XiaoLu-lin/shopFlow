package org.ysling.litemall.admin.model.profile.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改参数
 */
@Data
public class AdminUpdateBody implements Serializable {

    /**
     * 管理员账号
     */
    private String username;
    /**
     * 管理员昵称
     */
    private String nickName;

    /**
     * 管理员头像
     */
    private String avatar;

    /**
     * 管理员手机号
     */
    private String mobile;

    /**
     * 管理员邮箱
     */
    private String mail;

    /**
     * 管理员性别
     */
    private Short gender;


}
