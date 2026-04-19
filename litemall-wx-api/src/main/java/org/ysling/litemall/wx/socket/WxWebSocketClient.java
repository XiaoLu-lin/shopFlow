package org.ysling.litemall.wx.socket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ysling.litemall.core.utils.JacksonUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 自定义WebSocket客户端
 * @author Ysling
 */
public class WxWebSocketClient extends WebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(WxWebSocketClient.class);

    public AtomicBoolean isMessage = new AtomicBoolean(false);

    public WxWebSocketClient(String url) throws URISyntaxException {
        super(new URI(url));
        super.connect();
        //等待链接，最多等待5秒
        for (int i = 0; i < 5; i++) {
            if (!super.isOpen()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    /**
     * 建立链接
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.debug(" ws 服务正常打开！！");
    }

    /**
     * 收到消息
     */
    @Override
    public void onMessage(String text) {
        logger.debug(" ws 接收服务器推送的消息！！" + text);
        String errno = JacksonUtil.parseString(text, "errno");
        isMessage.set(Objects.equals(errno, "success"));
    }

    /**
     * 关闭链接
     */
    @Override
    public void onClose(int i, String s, boolean b) {
        logger.debug(" ws 客户端正常关闭！！");
    }

    /**
     * 出现异常
     */
    @Override
    public void onError(Exception e) {
        logger.debug(" ws 客户端连接出现错误！！");
    }
}
