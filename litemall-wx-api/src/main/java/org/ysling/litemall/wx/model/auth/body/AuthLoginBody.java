package org.ysling.litemall.wx.model.auth.body;

import lombok.Data;
import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class AuthLoginBody implements Serializable {

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
    /**
     * 验证码
     */
    @NotValue(message = "验证码不能为空")
    private String code;

}
