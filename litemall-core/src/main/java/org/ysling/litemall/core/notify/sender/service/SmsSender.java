package org.ysling.litemall.core.notify.sender.service;

import org.ysling.litemall.core.notify.model.SmsResult;

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
 * @author Ysling
 */
public interface SmsSender {

    /**
     * 发送短信息
     * @param phone   接收通知的电话号码
     * @param content 短消息内容
     */
    SmsResult send(String phone, String content);


    /**
     * 通过短信模版发送短信息
     *  @param phone      接收通知的电话号码
     * @param templateId 通知模板ID
     * @param params     通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    SmsResult sendWithTemplate(String phone, String templateId, String[] params);
}