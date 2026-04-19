package org.ysling.litemall.wx.model.auth.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信授权返回结果
 * @author Ysling
 */
@Data
public class WxAuthResult implements Serializable {

    /**
     * 微信openid
     */
    private String openId;

    /**
     * 微信sessionKey
     */
    private String sessionKey;

}
