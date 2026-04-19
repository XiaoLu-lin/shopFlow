package org.ysling.litemall.core.redis.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.connection.RedisConnection;
import org.ysling.litemall.core.redis.annotation.MessageHandler;
import org.ysling.litemall.core.redis.annotation.MessageListener;
import org.ysling.litemall.core.redis.cache.RedisCacheService;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

/**
 * redis消息队列topic处理方式 实现 订阅消息
 * 基于 list 的 lPush/brPop
 * @author Ysling
 */
@MessageHandler(value = MessageListener.Mode.TOPIC)
public class TopicMessageHandler extends AbstractMessageHandler {

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void invokeMessage(Method method) {
        Set<String> consumers = new HashSet<>();
        MessageListener annotation = method.getAnnotation(MessageListener.class);
        String topic = getTopic(annotation);
        RedisConnection connection = RedisCacheService.getConnection();
        Class<?> declaringClass = method.getDeclaringClass();
        Object bean = applicationContext.getBean(declaringClass);
        CompletableFuture.runAsync(()->{
            while (true) {
                List<byte[]> bytes = connection.bRPop(0, topic.getBytes());
                if (CollectionUtil.isNotEmpty(bytes)) {
                    if (bytes != null && bytes.get(1) != null) {
                        consumer(method, consumers, bean, bytes.get(1), annotation);
                    }
                }
            }
        });
    }

    private String getTopic(MessageListener annotation) {
        String value = annotation.value();
        String topic = annotation.topic();
        return StrUtil.isBlank(topic) ? value : topic;
    }

}
