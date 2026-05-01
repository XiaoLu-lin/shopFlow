package org.ysling.shopflow.admin.model.auth.body;

import lombok.Data;
import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

@Data
public class CaptchaBody implements Serializable {

    /**
     * 账号
     */
    @NotValue(message = "账号不能为空")
    private String username;

    /**
     * 租户appid
     */
    private String appid;

}
