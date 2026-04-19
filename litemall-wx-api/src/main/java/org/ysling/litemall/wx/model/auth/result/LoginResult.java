package org.ysling.litemall.wx.model.auth.result;

import lombok.Data;
import org.ysling.litemall.db.entity.AdminInfo;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class LoginResult implements Serializable {

    /**
     * 管理员信息
     */
    private AdminInfo adminInfo;

    /**
     * 管理员token
     */
    private String adminToken;


}
