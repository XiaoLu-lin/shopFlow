package org.ysling.shopflow.wx.model.auth.body;

import lombok.Data;
import org.ysling.shopflow.core.annotation.NotValue;

import java.io.Serializable;

/**
 * 旧 H5 普通用户登录请求参数
 * @author Ysling
 */
@Data
public class AuthLegacyLoginBody implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    @NotValue(message = "密码不能为空")
    private String password;

}
