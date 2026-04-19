package org.ysling.litemall.wx.socket;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.core.redis.annotation.MessageConsumer;
import org.ysling.litemall.core.redis.annotation.MessageListener;
import org.ysling.litemall.core.redis.util.Message;
import org.ysling.litemall.db.domain.LitemallMessage;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * redis消息队列消费者
 * 广播wedSocket消息找到分布式项目中的websocket链接对象并发布消息
 * @author Ysling
 */
@MessageConsumer
@Slf4j
public class WxWebSocketConsumer {

    @Autowired
    private ThreadPoolExecutor executorService;

    public static final String MESSAGE_PREFIX = "WX_MESSAGE_PUBSUB";

    @MessageListener(channel = MESSAGE_PREFIX, mode = MessageListener.Mode.PUBSUB, reuse = true)
    public void getMessage(Message<?> message) {
        executorService.submit(()->{
            String body = message.getContent().toString();
            String userId = message.getId();
            LitemallMessage toMessage = JSONUtil.toBean(body, LitemallMessage.class);
            if (toMessage == null) {
                return;
            }
            WxWebSocketContext.sendMessage(userId, toMessage);
            //打印日志
            log.info(String.format("[WebSocket] {%s}发送消息 = {%s}" , userId , body));
        });
    }


}