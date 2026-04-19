package org.ysling.litemall.admin.model.auth.result;

import lombok.Data;
import org.ysling.litemall.db.entity.AdminInfo;
import java.io.Serializable;

/**
 * 登录响应实体
 * @author Ysling
 */
@Data
public class LoginResult implements Serializable {

    /**
     * 管理员token
     */
    private String token;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 管理员信息
     */
    private AdminInfo adminInfo;

}
