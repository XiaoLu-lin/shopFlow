package org.ysling.litemall.admin.model.profile.body;

import lombok.Data;

import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 管理员修改密码
 * @author Ysling
 */
@Data
public class ProfilePasswordBody implements Serializable {

    /**
     * 旧密码
     */
    @NotValue(message = "旧密码不能为空")
    private String oldPassword;
    /**
     * 新密码
     */
    @NotValue(message = "新密码不能为空")
    private String newPassword;
    /**
     * 确认密码
     */
    @NotValue(message = "确认密码不能为空")
    private String confirmPassword;

}
