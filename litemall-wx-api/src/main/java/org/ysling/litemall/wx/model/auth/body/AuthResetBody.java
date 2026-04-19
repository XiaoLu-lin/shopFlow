package org.ysling.litemall.wx.model.auth.body;

import lombok.Data;

import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 密码重置
 * @author Ysling
 */
@Data
public class AuthResetBody implements Serializable {

    /**
     * 密码
     */
    @NotValue(message = "密码不能为空")
    private String password;
    /**
     * 手机号
     */
    @NotValue(message = "手机号不能为空")
    private String mobile;
    /**
     * 验证码
     */
    @NotValue(message = "验证码不能为空")
    private String code;

}
