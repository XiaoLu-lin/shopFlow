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
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.qcloud.cos.utils.Jackson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.notify.model.SmsResult;
import org.ysling.litemall.core.notify.sender.service.SmsSender;
import org.ysling.litemall.core.utils.JacksonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信服务
 */
@Slf4j
@Data
public class AliyunSmsSender implements SmsSender {

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String sign;


    @Override
    public SmsResult send(String phone, String content) {
        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }


    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", this.regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", this.sign);
        request.putQueryParameter("TemplateCode", templateId);
        /*
          NOTE：阿里云短信和腾讯云短信这里存在不一致
          腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
          阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
          为了保持统一，我们假定阿里云短信里面的参数是code，code1，code2...

          如果开发者在阿里云短信申请的模板参数是其他命名，请开发者自行调整这里的代码，或者直接写死。
         */
        String templateParam = "{}";
        if(params.length == 1){
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            templateParam = JacksonUtil.toJson(data);
        }
        else if(params.length > 1){
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            for(int i = 1; i < params.length; i++){
                data.put("code" + i, params[i]);
            }
            templateParam = JacksonUtil.toJson(data);
        }
        request.putQueryParameter("TemplateParam", templateParam);

        try {
            CommonResponse response = client.getCommonResponse(request);
            SmsResult smsResult = new SmsResult();
            smsResult.setResult(response);
            String code =  Jackson.jsonNodeOf(response.getData()).get("Code").asText();
            String okCode = "OK";
            if (response.getHttpResponse().isSuccess() && okCode.equals(code) ){
                smsResult.setSuccessful(true);
            }else {
                smsResult.setSuccessful(false);
                log.error("短信发送失败："+response.getData());
            }

            return smsResult;
        } catch (ClientException e) {
            e.printStackTrace();
        }

        SmsResult smsResult = new SmsResult();
        smsResult.setSuccessful(false);
        return smsResult;
    }
}
