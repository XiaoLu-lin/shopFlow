package org.ysling.litemall.wx.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.chatgpt.ChatGPTClient;
import org.ysling.litemall.core.utils.token.TokenManager;
import org.ysling.litemall.db.domain.LitemallMessage;
import org.ysling.litemall.db.service.IMessageService;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 配置接入点
 * ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @author Ysling
 */
@Component
@ServerEndpoint(value = "/websocket/{token}")
@Slf4j
public class WxWebSocketServer {

    private static IMessageService messageService;

    private static ThreadPoolExecutor executorService;

    @Autowired
    public void setMessageService(IMessageService messageService) {
        WxWebSocketServer.messageService = messageService;
    }

    @Autowired
    public void setExecutorService(ThreadPoolExecutor executorService) {
        WxWebSocketServer.executorService = executorService;
    }

    /**
     * 打开连接触发事件
     * @param token 用户授权
     * @param session 用户会话
     */
    @OnOpen
    public void onOpen(@PathParam("token") String token, Session session) {
        //根据用户token获取用户id
        String userId = TokenManager.getUserId(token);
        if (userId == null) {
            throw new RuntimeException("登陆已失效，请重新登陆");
        }
        WxWebSocketContext.add(session, userId);
        //打印日志
        log.info(String.format("[WebSocket] 连接成功，当前连接人数为: = {%s}" , WxWebSocketContext.getUserSize()));
    }

    /**
     * 收到客户端消息后调用的方法
     * @param body 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(@RequestBody String body, Session session) {
        String userId = WxWebSocketContext.getUserId(session);
        LitemallMessage message = WxWebSocketContext.getMessage(body, userId);
        if (message == null) {
            return;
        }
        //异步处理消息
        executorService.submit(()->{
            //切换租户
            TenantContextHolder.setLocalTenantId(message.getTenantId());
            //保存消息记录
            message.setTenantId(null);
            messageService.add(message);
            //给自己和对方发送消息
            WxWebSocketContext.sendMessage(message, session, message.getReceiveUserId());
            //判断是否是ChatGPT
            if (message.getReceiveUserId().equals(ChatGPTClient.CHAT_GPT_USERID)){
                //发送请求并获取消息实体
                LitemallMessage resultMessage = ChatGPTClient.getMessage(userId, message.getContent());
                //保存历史记录
                messageService.add(resultMessage);
                //发送消息
                WxWebSocketContext.sendMessage(userId, resultMessage);
            }
            //清除线程缓存
            TenantContextHolder.removeLocalTenantId();
        });
        //打印日志
        log.info(String.format("[WebSocket] {%s}发送消息 = {%s}" , WxWebSocketContext.getUserId(session) , message));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        WxWebSocketContext.remove(session);
        log.info(String.format("[WebSocket] 退出成功，当前连接人数为：={%s}", WxWebSocketContext.getUserSize()));
    }

    /**
     * 监听错误
     *
     * @param session session
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        WxWebSocketContext.sendHintTo(session, error.getMessage());
        log.info(String.format("[WebSocket] {%s} 信息发送错误{%s}", WxWebSocketContext.getUserId(session), error.getMessage()));
    }

}
