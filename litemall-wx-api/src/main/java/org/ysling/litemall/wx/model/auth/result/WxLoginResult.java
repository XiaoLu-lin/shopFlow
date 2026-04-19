package org.ysling.litemall.wx.model.auth.result;

import lombok.Data;
import org.ysling.litemall.db.entity.UserInfo;

import java.io.Serializable;

/**
 * 登录返回参数
 * @author Ysling
 */
@Data
public class WxLoginResult implements Serializable {

    /**
     * 用户token
     */
    private String userToken;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

}
