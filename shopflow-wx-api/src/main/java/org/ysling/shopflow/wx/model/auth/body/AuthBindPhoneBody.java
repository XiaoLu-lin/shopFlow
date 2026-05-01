package org.ysling.shopflow.wx.model.auth.body;

import lombok.Data;
import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 微信绑定手机
 * @author Ysling
 */
@Data
public class AuthBindPhoneBody implements Serializable {

    /**
     * 密钥
     */
    @NotValue(message = "密钥不能为空")
    private String iv;

    /**
     * 密钥
     */
    @NotValue(message = "密钥不能为空")
    private String encryptedData;

}
