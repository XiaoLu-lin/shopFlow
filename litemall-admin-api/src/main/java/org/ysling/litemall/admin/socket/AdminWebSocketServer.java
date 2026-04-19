package org.ysling.litemall.admin.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;

/**
 * @author Ysling
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/admin/{socketInfo}")
public class AdminWebSocketServer {

    /**
     * 打开连接触发事件
     * @param socketInfo 用户授权
     * @param session 用户会话
     */
    @OnOpen
    public void onOpen(@PathParam("socketInfo") String socketInfo, Session session) {
        if (socketInfo == null){
            AdminWebSocketContext.sendHintTo(session ,"链接失败");
            return;
        }
        AdminWebSocketContext.add(session, socketInfo);
        log.info(String.format("[WebSocket] 连接成功，当前连接人数为：={%s}",AdminWebSocketContext.getUserSize()));
    }

    /**
     * 收到消息触发事件
     * @param body 信息
     */
    @OnMessage
    public void onMessage(@RequestBody String body, Session session) {
        String userId = AdminWebSocketContext.getUserId(session);
        String[] split = userId.split("=");
        String type = split[1];
        String authCode = split[0];
        if (Objects.equals(type, "admin")){
            String socketInfo = authCode + "=wx";
            AdminWebSocketContext.sendMessage(session ,body , socketInfo);
        }
        if (Objects.equals(type, "wx")){
            String socketInfo = authCode + "=admin";
            AdminWebSocketContext.sendMessage(session ,body , socketInfo);
        }
        //打印日志
        log.info(String.format("[WebSocket] {%s}发送消息 = {%s}" , userId , body));
    }


    /**
     * 关闭连接触发事件
     */
    @OnClose
    public void onClose(Session session) {
        // 下线，并且通知到其他人
        AdminWebSocketContext.remove(session);
        log.info(String.format("[WebSocket] 退出成功，当前连接人数为：={%s}", AdminWebSocketContext.getUserSize()));
    }

    /**
     * 监听错误
     * @param session session
     * @param error   错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        AdminWebSocketContext.sendHintTo(session, error.getMessage());
        log.info(String.format("[WebSocket] {%s} 信息发送错误{%s}", AdminWebSocketContext.getUserId(session) , error.getMessage()));
    }

}
