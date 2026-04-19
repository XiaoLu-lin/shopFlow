package org.ysling.litemall.core.utils.chatgpt;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.service.StorageCoreService;
import org.ysling.litemall.db.domain.LitemallMessage;

import java.util.*;

/**
 * ChatGPT3.5调用工具类
 */
@Component
public class ChatGPTClient {

    private static final String SELECT_KEY = "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";

    public static final String CHAT_GPT_USERID = "0000000000000000000";

    public static final String CHAT_GPT_NICKNAME = "ChatGPT";

    public static final String CHAT_GPT_AVATAR = "https://th.bing.com/th?id=OSK.2fe5b3f3f141834f896fe8a9ffe3a1dc&w=148&h=148&c=7&o=6&dpr=1.8&pid=SANGAM";

    /**ChatGPT自然语言处理接口*/
    public static final String CHAT_REQUEST = "https://api.openai.com/v1/chat/completions";
    /**ChatGPT图片处理接口*/
    public static final String CHAT_IMAGE_REQUEST = "https://api.openai.com/v1/images/generations";

    private static StorageCoreService storageCoreService;

    @Autowired
    public void setStorageCoreService(StorageCoreService storageCoreService) {
        ChatGPTClient.storageCoreService = storageCoreService;
    }

    /**
     * ChatGPT请求接口
     * @param content 自然语言
     * @return AI
     */
    public static ChatResult chatSend(String content){
        if (content.contains("图片")){
            HashMap<String, Object> body = new HashMap<>();
            body.put("prompt", content);
            body.put("n" , 1);
            body.put("size" , "256x256");
            return chatRequest(CHAT_IMAGE_REQUEST, body);
        }
        HashMap<String, Object> body = new HashMap<>();
        ChatResult.Message message = new ChatResult.Message();
        message.setRole(ChatResult.RoleEnum.user.name());
        message.setContent(content);
        body.put("messages", Collections.singletonList(message));
        body.put("max_tokens" , 4000);
        body.put("model" , "gpt-3.5-turbo");
        return chatRequest(CHAT_REQUEST, body);
    }

    /**
     * 根据AI返回值构建消息
     * @param userId        用户ID
     * @param chatResult    AI返回值
     * @return 消息体
     */
    public static LitemallMessage getResultMessage(String userId, ChatResult chatResult){
        LitemallMessage resultMessage = new LitemallMessage();
        resultMessage.setNickName(CHAT_GPT_NICKNAME);
        resultMessage.setAvatarUrl(CHAT_GPT_AVATAR);
        StringBuilder content = new StringBuilder();
        if (chatResult.getChoices() != null){
            chatResult.getChoices().forEach(item->content.append(item.getMessage().getContent()));
        }
        resultMessage.setContent(content.toString());
        ArrayList<String> picUrls = new ArrayList<>();
        if (chatResult.getData() != null){
            chatResult.getData().forEach(item->{
                String storageUrl = storageCoreService.uploadStorageUrl(item.getUrl());
                picUrls.add(storageUrl);
            });
        }
        resultMessage.setPicUrls(picUrls.toArray(new String[0]));
        resultMessage.setReceiveUserId(userId);
        resultMessage.setSendUserId(CHAT_GPT_USERID);
        return resultMessage;
    }

    /**
     * 根据AI返回值构建消息
     * @param userId        用户ID
     * @return 消息体
     */
    public static LitemallMessage getMessage(String userId, String content){
        LitemallMessage resultMessage = new LitemallMessage();
        resultMessage.setNickName(CHAT_GPT_NICKNAME);
        resultMessage.setAvatarUrl(CHAT_GPT_AVATAR);
        resultMessage.setContent(ChatGPTUtil.send(content));
        resultMessage.setPicUrls(new String[0]);
        resultMessage.setReceiveUserId(userId);
        resultMessage.setSendUserId(CHAT_GPT_USERID);
        return resultMessage;
    }

    /**
     * 上下文请求
     * @param messages 历史消息
     * @return 回复
     */
    public static ChatResult chatSend(List<ChatResult.Message> messages){
        HashMap<String, Object> body = new HashMap<>();
        body.put("messages", messages);
        body.put("max_tokens" , 4000);
        body.put("model" , "gpt-3.5-turbo");
        return chatRequest(CHAT_REQUEST, body);
    }

    /**
     * 整合历史聊天记录
     * @param historyMessage 历史聊天记录
     * @return 回复
     */
    public static ChatResult chatSendMessage(List<LitemallMessage> historyMessage){
        List<ChatResult.Message> messages = new ArrayList<>();
        for (LitemallMessage history :historyMessage) {
            ChatResult.Message chatMessage = new ChatResult.Message();
            if (history.getSendUserId().equals(CHAT_GPT_USERID)){
                chatMessage.setRole(ChatResult.RoleEnum.assistant.name());
            } else {
                chatMessage.setRole(ChatResult.RoleEnum.user.name());
            }
            chatMessage.setContent(history.getContent());
            messages.add(chatMessage);
        }
        return chatSend(messages);
    }

    /**
     * 请求ChatGPT3.5接口
     * @param body 请求参数 ['256x256', '512x512', '1024x1024'] - 'size
     * @return AI回调
     */
    private static ChatResult chatRequest(String requestUrl ,Map<String, Object> body){
        String result = HttpRequest.post(requestUrl)
                .header("Content-Type", "application/json")
//                .setHttpProxy("127.0.0.1", 7890)
                .header(" Authorization", "Bearer " + SELECT_KEY)
                .setMaxRedirectCount(3)
                .body(JSONUtil.toJsonStr(body))
                .execute().body();
        ChatResult chatResult = JSONUtil.toBean(result, ChatResult.class);
        if (chatResult.getError() != null){
            throw new RuntimeException(chatResult.getError().getMessage());
        }
        return chatResult;
    }

}
