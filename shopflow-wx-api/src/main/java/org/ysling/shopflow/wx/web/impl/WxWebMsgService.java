package org.ysling.shopflow.wx.web.impl;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaXmlOutMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * 微信消息推送配置
 */
@Slf4j
@Service
public class WxWebMsgService {

    @Autowired
    private WxMaService wxMaService;

    /**
     * 消息校验，确定是微信发送的消息
     * @param signature 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return 随机字符串
     */
    public String doGet(String signature, String timestamp, String nonce, String echostr) {
        return wxMaService.checkSignature(timestamp, nonce, signature) ? echostr : null;
    }

    /**
     * 微信小程序事件推送
     */
    public String doPost(String requestBody, String nonce, String timestamp, String msgSignature, String encryptType) throws IOException {
        // 解密消息
        WxMaMessage wxMaMessage = getWxMaMessage(requestBody, msgSignature, encryptType, timestamp, nonce);
        log.info("收到消息：{}", wxMaMessage);
        // 转到客服
        WxMaXmlOutMessage wxMaXmlOutMessage = new WxMaXmlOutMessage();
        wxMaXmlOutMessage.setToUserName(wxMaMessage.getFromUser());
        wxMaXmlOutMessage.setFromUserName(wxMaMessage.getToUser());
        // 转发到客服
        wxMaXmlOutMessage.setMsgType("transfer_customer_service");
        wxMaXmlOutMessage.setCreateTime(System.currentTimeMillis());
        return toWxMaOutMessage(wxMaXmlOutMessage, encryptType);
    }

    /**
     * 解析消息
     */
    public WxMaMessage getWxMaMessage(String requestBody, String msgSignature, String encryptType, String timestamp, String nonce) {
        // 明文传输
        if (StringUtils.isBlank(encryptType)) {
            // json
            if (WxMaConstants.MsgDataFormat.JSON.equals(wxMaService.getWxMaConfig().getMsgDataFormat())) {
                return WxMaMessage.fromJson(requestBody);
            } else {
                return WxMaMessage.fromXml(requestBody);
            }
        }
        // aes加密
        else if ("aes".equals(encryptType)) {
            if (WxMaConstants.MsgDataFormat.JSON.equals(wxMaService.getWxMaConfig().getMsgDataFormat())) {
                // json
                return WxMaMessage.fromEncryptedJson(requestBody, wxMaService.getWxMaConfig());
            } else {
                // xml
                return WxMaMessage.fromEncryptedXml(requestBody, wxMaService.getWxMaConfig(), timestamp, nonce, msgSignature);
            }
        }
        throw new RuntimeException("不可识别的消息加密方式");
    }

    /**
     * 返回给微信服务器的消息
     */
    public String toWxMaOutMessage(WxMaXmlOutMessage wxMaXmlOutMessage, String encryptType) {
        // 明文传输
        if (StringUtils.isBlank(encryptType)) {
            return wxMaXmlOutMessage.toXml();
        }
        // aes加密
        else if ("aes".equals(encryptType)) {
            return wxMaXmlOutMessage.toEncryptedXml(wxMaService.getWxMaConfig());
        }
        throw new RuntimeException("不可识别的消息加密方式");
    }

}
