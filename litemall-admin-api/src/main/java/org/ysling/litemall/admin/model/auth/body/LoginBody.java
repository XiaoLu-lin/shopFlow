package org.ysling.litemall.admin.model.auth.body;

import lombok.Data;

import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 登录请求参数
 * @author Ysling
 */
@Data
public class LoginBody implements Serializable {

    /**
     * 用户名
     */
    @NotValue(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotValue(message = "密码不能为空")
    private String password;

}
