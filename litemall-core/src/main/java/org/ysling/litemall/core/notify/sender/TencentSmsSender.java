package org.ysling.litemall.core.notify.sender;
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
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.notify.model.SmsResult;
import org.ysling.litemall.core.notify.sender.service.SmsSender;

import java.io.IOException;

/**
 * 腾讯云短信服务
 * @author Ysling
 */
@Slf4j
@Data
public class TencentSmsSender implements SmsSender {

    /**
     * 签名
     */
    private String sign;
    /**
     * 短信服务
     */
    private SmsSingleSender sender;


    @Override
    public SmsResult send(String phone, String content) {
        try {
            SmsSingleSenderResult result = sender.send(0, "86", phone, content, "", "");
            log.debug(result.toString());
            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            log.error(e.getMessage(), e);
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }


    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        try {
            SmsSingleSenderResult result = sender.sendWithParam("86", phone, Integer.parseInt(templateId), params, this.sign, "", "");
            log.debug(result.toString());
            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            log.error(e.getMessage(), e);
        }
        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }

}
