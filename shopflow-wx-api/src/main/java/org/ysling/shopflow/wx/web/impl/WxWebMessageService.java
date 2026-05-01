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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.utils.chatgpt.ChatGPTClient;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowMessage;
import org.ysling.shopflow.wx.model.message.body.HistoryMessageBody;
import org.ysling.shopflow.wx.service.WxMessageService;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户聊天列表
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebMessageService {

    @Autowired
    private WxMessageService messageService;

    /**
     * 获取聊天记录
     * @param userId 用户ID
     * @return 聊天记录
     */
    public Object getMessageList(String userId){
        //chatGPT对话
        ShopflowMessage chatMessage = new ShopflowMessage();
        chatMessage.setSendUserId(ChatGPTClient.CHAT_GPT_USERID);
        chatMessage.setNickName(ChatGPTClient.CHAT_GPT_NICKNAME);
        chatMessage.setAvatarUrl(ChatGPTClient.CHAT_GPT_AVATAR);
        chatMessage.setPicUrls(new String[0]);
        chatMessage.setContent("你好，我是ChatGPT.");
        chatMessage.setAddTime(LocalDateTime.now());
        chatMessage.setReceiveUserId(userId);

        //过滤重复数据 TODO 暂时只能获取给自己发过信息的列表
        HashMap<String, ShopflowMessage> receiveMessageMap = new HashMap<>();
        receiveMessageMap.put(chatMessage.getSendUserId(), chatMessage);
        List<ShopflowMessage> receiveList = messageService.queryByReceiveUserId(userId);
        for (ShopflowMessage message :receiveList) {
            receiveMessageMap.put(message.getSendUserId(), message);
        }

        //合并历史消息记录
        ArrayList<ShopflowMessage> messageList = new ArrayList<>(receiveMessageMap.values());
        for (ShopflowMessage message :messageList) {
            if (!StringUtils.hasText(message.getContent())) {
                if (message.getPicUrls().length != 0){
                    message.setContent("[图片]......");
                }else {
                    message.setContent("......");
                }
            }
        }
        //历史消息排序
        messageList.sort(Comparator.comparing(ShopflowMessage::getAddTime).reversed());
        //返回信息列表
        return ResponseUtil.okList(messageList);
    }


    /**
     * 获取历史聊天记录
     * @param userId 用户ID
     * @return 聊天记录
     */
    public Object getHistoryMessageList(String userId, HistoryMessageBody body){
        //获取聊天记录
        List<ShopflowMessage> historyMessage = messageService.getHistoryMessage(userId , body);
        //历史消息排序
        historyMessage.sort(Comparator.comparing(ShopflowMessage::getAddTime));
        return ResponseUtil.okList(historyMessage);
    }

    /**
     * 删除聊天记录
     * @param userId 用户ID
     * @param sendUserId 发送用户
     * @return 成功
     */
    public Object delete(String userId, String sendUserId){
        if (Objects.isNull(userId)) {
            return ResponseUtil.unlogin();
        }
        messageService.deleteMessage(userId , sendUserId);
        messageService.deleteMessage(sendUserId, userId);
        return ResponseUtil.ok();
    }

}
