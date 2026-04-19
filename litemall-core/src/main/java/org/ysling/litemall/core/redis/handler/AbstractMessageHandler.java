package org.ysling.litemall.core.redis.handler;

import cn.hutool.json.JSONUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.ysling.litemall.core.redis.annotation.MessageListener;
import org.ysling.litemall.core.redis.util.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * redis消息队列处理接口
 */
@Slf4j
public abstract class AbstractMessageHandler implements ApplicationContextAware {

    /**bean上下文*/
    protected ApplicationContext applicationContext;
    /**value序列化解码*/
    protected JdkSerializationRedisSerializer redisSerializer;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.redisSerializer = new JdkSerializationRedisSerializer();
    }

    /**
     * 执行消息监听逻辑
     * @param method 方法
     */
    public abstract void invokeMessage(Method method);

    /**
     * 通过反射 执行 Method 方法
     * @param method    方法
     * @param message   消息
     * @param bean      @MessageConsumer 修饰的 bean 对象
     */
    protected void invokeMethod(Method method, Message<?> message, Object bean) {
        try {
            method.invoke(bean, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将byte转换为对象
     * @param bytes 消息数据
     * @return Message对象
     */
    protected Message<?> getMessage(byte[] bytes) {
        Object deserialize = redisSerializer.deserialize(bytes);
        String message = JSONUtil.toJsonStr(deserialize);
        return JSONUtil.toBean(message, Message.class);
    }

    /**
     * 获取消息
     * @param method        方法
     * @param consumers     消费者
     * @param bean          @MessageConsumer 修饰的 bean 对象
     * @param message       消息
     * @param annotation    消息监听注解
     */
    protected void consumer(Method method, Set<String> consumers, Object bean, byte[] message, MessageListener annotation) {
        Message<?> msg = getMessage(message);
        if (annotation.reuse()){
            invokeMethod(method, msg, bean);
        } else if (consumers.add(msg.getId())){
            invokeMethod(method, msg, bean);
        } else {
            log.error("消息已经被消费 {}", msg);
        }
    }


}
