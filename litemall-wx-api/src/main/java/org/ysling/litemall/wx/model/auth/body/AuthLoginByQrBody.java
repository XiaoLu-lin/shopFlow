package org.ysling.litemall.wx.model.auth.body;

import lombok.Data;
import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class AuthLoginByQrBody implements Serializable {

    /**
     * 授权码
     */
    @NotValue(message = "授权码不能为空")
    private String authCode;

}
