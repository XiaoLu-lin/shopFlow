package org.ysling.litemall.core.weixin.config;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ysling
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "litemall.wx")
public class WxProperties {

    /**
     * 消息推送token
     */
    private String token;
    /**
     * 消息推送key
     */
    private String aesKey;
    /**
     * 小程序appId
     */
    private String appId;
    /**
     * 小程序密钥
     */
    private String appSecret;
    /**
     * 微信商户号
     */
    private String mchId;
    /**
     * 微信商户密钥
     */
    private String mchKey;
    /**
     * 微信支付v3密钥
     */
    private String apiV3Key;
    /**
     * 支付回调地址
     */
    private String notifyUrl;
    /**
     * 微信支付证书地址.p12
     */
    private String keyPath;
    /**
     * 微信支付证书地址.cert
     */
    private String privateCertPath;
    /**
     * 微信支付证书地址.key
     */
    private String privateKeyPath;


}
