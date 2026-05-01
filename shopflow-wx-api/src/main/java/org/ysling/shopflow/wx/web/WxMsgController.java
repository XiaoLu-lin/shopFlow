package org.ysling.shopflow.wx.web;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.wx.web.impl.WxWebMsgService;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 微信消息推送配置
 */
@Slf4j
@RestController
@RequestMapping(value = "/wx/msg")
@Validated
public class WxMsgController {

    @Autowired
    private WxWebMsgService msgService;

    /**
     * 消息校验，确定是微信发送的消息
     * @param signature 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return 随机字符串
     */
    @GetMapping(value = "/welcome" , produces = "text/plain;charset=utf-8")
    public String doGet(@RequestParam(required = false) String signature,
                        @RequestParam(required = false) String timestamp,
                        @RequestParam(required = false) String nonce,
                        @RequestParam(required = false) String echostr) {
        return msgService.doGet(signature, timestamp, nonce, echostr);
    }

    /**
     * 微信小程序事件推送
     */
    @PostMapping(value = "/welcome" , produces = "text/plain;charset=utf-8")
    public String doPost(@Valid @RequestBody String requestBody,
                         @RequestParam("nonce") String nonce,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("msg_signature") String msgSignature,
                         @RequestParam("encrypt_type") String encryptType) throws IOException {
        return msgService.doPost(requestBody, nonce, timestamp, msgSignature, encryptType);
    }

}
