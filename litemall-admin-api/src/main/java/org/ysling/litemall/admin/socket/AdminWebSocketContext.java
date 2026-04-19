package org.ysling.litemall.admin.socket;

import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.redis.cache.RedisCacheService;
import org.ysling.litemall.core.redis.util.Message;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话上下文工具
 * @author Ysling
 */
@Slf4j
public class AdminWebSocketContext {

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
    public static boolean sendHintTo(Session session, Object content) {
        String messageText = buildTextMessage(content);
        return sendMessage(session, messageText);
    }

    /**
     * 广播发送消息给所有在线用户
     *
     * @param content 消息体
     * @param me      当前消息的发送者，不会将消息发送给自己
     */
    public static boolean broadcast(Object content, Session me) {
        // 创建消息
        String messageText = buildTextMessage(content);
        // 遍历 SESSION_USER_MAP ，进行逐个发送
        for (Session session : SESSION_USER_MAP.keySet()) {
            if (!session.equals(me)) {
                sendTextMessage(session, messageText);
            }
        }
        return true;
    }

    /**
     * 发送消息给单个用户的 Session
     *
     * @param session Session
     * @param content 消息体
     */
    public static boolean sendMessage(Session session, Object content) {
        if (content == null){
            log.error("==> message消息不能为null");
            return false;
        }
        // 创建消息
        String messageText = buildTextMessage(content);
        // 遍历给单个 Session ，进行逐个发送
        return sendTextMessage(session, messageText);
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId    指定用户
     * @param content 消息体
     */
    public static boolean sendMessage(String userId, Object content) {
        // 获得用户对应的 Session
        Session session = USER_SESSION_MAP.get(userId);
        if (session == null) {
            log.error("==> user({}) 不存在对应的 session", userId);
            return false;
        }
        // 发送消息
        return sendMessage(session, content);
    }

    /**
     * 发送日志
     * @param readLines 日志列表
     */
    public static void sendLog(String userId, List<String> readLines){
        StringBuffer buffer = new StringBuffer();
        for (String content : readLines) {
            buffer.append("<p style=\"font-size: 14px;\">");
            if (content.contains("\t")){
                buffer.append("&nbsp; &nbsp; &nbsp;");
            }
            buffer.append(content).append("</p>");
        }
        if (readLines.size() > 0){
            if (!AdminWebSocketContext.sendMessage(userId ,buffer)){
                throw new RuntimeException("链接已关闭");
            }
        }
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId    指定用户
     * @param content 消息体
     */
    public static boolean sendMessage(String userId, Object content, boolean convertSend) {
        // 获得用户对应的 Session
        Session session = USER_SESSION_MAP.get(userId);
        if (session == null) {
            log.error("==> user({}) 不存在对应的 session", userId);
            if (convertSend){
                //消息发送失败将消息广播出去
                Message<String> message = new Message<>();
                String textMessage = buildTextMessage(content);
                message.setId(userId);
                message.setContent(textMessage);
                RedisCacheService.convertAndSend(AdminWebSocketConsumer.MESSAGE_PREFIX, message);
                return true;
            }
            return false;
        }
        // 发送消息
        return sendMessage(session, content);
    }

    /**
     * 向自己和指定用户发送信息
     * @param content 消息对象
     * @param socketInfo 发送用户
     */
    public static boolean sendMessage(Session session ,Object content, String socketInfo) {
        return sendMessage(session, content) && sendMessage(socketInfo, content, true);
    }

    /**
     * 构建完整的消息
     * @param content 消息体
     * @return 消息
     */
    private static String buildTextMessage(Object content) {
        return JacksonUtil.toJson(ResponseUtil.ok(content));
    }

    /**
     * 真正发送消息
     *
     * @param session     Session
     * @param messageText 消息
     */
    public static boolean sendTextMessage(Session session, String messageText) {
        if (session == null) {
            log.error("===> session 为 null");
            return false;
        }
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            log.error("===> session.basic 为 null");
            return false;
        }
        try {
            basic.sendText(messageText);
        } catch (IOException e) {
            log.error("===> session: {} 发送消息: {} 发生异常", session, messageText, e);
            return false;
        }
        return true;
    }


}
