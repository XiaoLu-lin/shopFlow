package org.ysling.shopflow.wx.model.auth.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 账号注册请求参数
 * @author Ysling
 */
@Data
public class AuthRegisterBody implements Serializable {

    /**
     * 用户名（旧 H5 兼容可选）
     */
    private String username;

    /**
     * 手机号
     */
    @NotValue(message = "手机号不能为空")
    private String mobile;

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

    /**
     * 微信授权（非必填）
     */
    private String wxCode;

}
