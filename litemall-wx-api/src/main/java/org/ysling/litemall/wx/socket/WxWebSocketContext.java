package org.ysling.litemall.wx.socket;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.redis.cache.RedisCacheService;
import org.ysling.litemall.core.redis.util.Message;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.core.utils.token.TokenManager;
import org.ysling.litemall.db.domain.LitemallMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话上下文工具
 */
@Slf4j
public class WxWebSocketContext {

    /**
     * Session 与用户的映射
     */
    private static final Map<Session, String> SESSION_USER_MAP = new ConcurrentHashMap<>();
    /**
     * 用户与 Session 的映射
     */
    private static final Map<String, Session> USER_SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * 获取在线数量
     */
    public static Integer getUserSize(){
        return USER_SESSION_MAP.size();
    }
    /**
     * 获取用户ID
     */
    public static String getUserId(Session session){
        return SESSION_USER_MAP.get(session);
    }
    /**
     * 获取session
     */
    public static Session getSession(String userId){
        return USER_SESSION_MAP.get(userId);
    }

    /**
     * 添加 Session 在这个方法中，会绑定用户和 Session 之间的映射
     *
     * @param session Session
     * @param userId    用户
     */
    public static void add(Session session, String userId) {
        // 更新 USER_SESSION_MAP
        USER_SESSION_MAP.put(userId, session);
        // 更新 SESSION_USER_MAP
        SESSION_USER_MAP.put(session, userId);
    }

    /**
     * 移除 Session
     *
     * @param session Session
     */
    public static void remove(Session session) {
        // 从 SESSION_USER_MAP 中移除
        String user = SESSION_USER_MAP.remove(session);
        // 从 USER_SESSION_MAP 中移除
        if (user != null && user.length() > 0) {
            USER_SESSION_MAP.remove(user);
        }
    }

    /**
     * 向指定用户发送信息提示
     * @param session 用户会话
     * @param content 提示信息
     */
    public static void sendHintTo(Session session, String content) {
        LitemallMessage message = new LitemallMessage();
        message.setContent(content);
        sendMessage(session, message);
    }

    /**
     * 向指定用户发送信息提示
     * @param userId 用户会话
     * @param content 提示信息
     */
    public static void sendHintTo(String userId, String content) {
        LitemallMessage message = new LitemallMessage();
        message.setContent(content);
        sendMessage(userId, message);
    }


    /**
     * 广播发送消息给所有在线用户
     *
     * @param message 消息体
     * @param me      当前消息的发送者，不会将消息发送给自己
     */
    public static void broadcast(LitemallMessage message, Session me) {
        // 创建消息
        String messageText = buildTextMessage(message);
        // 遍历 SESSION_USER_MAP ，进行逐个发送
        for (Session session : SESSION_USER_MAP.keySet()) {
            if (!session.equals(me)) {
                sendTextMessage(session, messageText);
            }
        }
    }

    /**
     * 发送消息给单个用户的 Session
     *
     * @param session Session
     * @param message 消息体
     */
    public static void sendMessage(Session session, LitemallMessage message) {
        if (message == null){
            log.error("==> message消息不能为null");
            return;
        }
        // 创建消息
        String messageText = buildTextMessage(message);
        // 遍历给单个 Session ，进行逐个发送
        sendTextMessage(session, messageText);
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId    指定用户
     * @param message 消息体
     */
    public static void sendMessage(String userId, LitemallMessage message) {
        // 获得用户对应的 Session
        Session session = USER_SESSION_MAP.get(userId);
        if (session == null) {
            log.error("==> user({}) 不存在对应的 session", userId);
            return;
        }
        // 发送消息
        sendMessage(session, message);
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId    指定用户
     * @param message   消息体
     * @param convertSend 未找到session是否广播
     */
    public static void sendMessage(String userId, LitemallMessage message, boolean convertSend) {
        // 获得用户对应的 Session
        Session session = USER_SESSION_MAP.get(userId);
        if (session == null) {
            log.error("==> user({}) 不存在对应的 session", userId);
            if (convertSend){
                //消息发送失败将消息广播出去
                Message<String> convertMessage = new Message<>();
                String textMessage = JacksonUtil.toJson(message);
                convertMessage.setId(userId);
                convertMessage.setContent(textMessage);
                convertMessage.setTenantId(message.getTenantId());
                RedisCacheService.convertAndSend(WxWebSocketConsumer.MESSAGE_PREFIX, convertMessage);
            }
            return;
        }
        // 发送消息
        sendMessage(session, message);
    }

    /**
     * 构建完整的消息
     * @param message 消息体
     * @return 消息
     */
    private static String buildTextMessage(LitemallMessage message) {
        return JacksonUtil.toJson(Lists.newArrayList(message));
    }

    /**
     * 向自己和指定用户发送信息
     * @param message 消息对象
     * @param session 发送用户
     * @param receiveUserId 接收用户
     */
    public static void sendMessage(LitemallMessage message ,Session session, String receiveUserId) {
        sendMessage(session , message);
        sendMessage(receiveUserId , message , true);
    }

    /**
     * 真正发送消息
     *
     * @param session     Session
     * @param messageText 消息
     */
    private static void sendTextMessage(Session session, String messageText) {
        if (session == null) {
            log.error("===> session 为 null");
            return;
        }
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            log.error("===> session.basic 为 null");
            return;
        }
        try {
            basic.sendText(messageText);
        } catch (IOException e) {
            log.error("===> session: {} 发送消息: {} 发生异常", session, messageText, e);
        }
    }

    /**
     * 获取消息对象
     * @param body  json参数
     * @param userId 发送用户ID
     * @return 消息对象
     */
    public static LitemallMessage getMessage(String body, String userId) {
        //心跳校验
        String token = JacksonUtil.parseString(body, "token");
        if (token != null) {
            return null;
        }
        //解析json
        LitemallMessage message;
        try {
            message = JSONUtil.toBean(body, LitemallMessage.class);
            message.setSendUserId(userId);
            //禁止给自己发送信息
            if (userId.equals(message.getReceiveUserId())){
                throw new RuntimeException("禁止给自己发送信息");
            }
            //如果消息发送数据为null则不发送
            if (!ObjectUtils.allNotNull(message.getReceiveUserId(),  userId)){
                throw new RuntimeException("未找到消息接收对象");
            }
            //用户基本信息
            if (!ObjectUtils.allNotNull(message.getAvatarUrl(), message.getNickName())){
                throw new RuntimeException("用户信息获取失败");
            }
            //获取用户所属租户
            message.setTenantId(TokenManager.getTenantId(message.getTenantId()));
            if (!StringUtils.hasText(message.getTenantId())){
                throw new RuntimeException("用户信息获取失败");
            }
            //禁止发送空消息
            if (!StringUtils.hasText(message.getContent())) {
                if ((message.getPicUrls() == null || message.getPicUrls().length <= 0)){
                    throw new RuntimeException("发送信息不能为空");
                }
            }
        } catch (Exception e){
            sendHintTo(userId, e.getMessage());
            return null;
        }
        return message;
    }



}
