package org.ysling.litemall.core.redis.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * redis消息队列处理器注解，不同的类型使用不同的注解标准
 * @author Ysling
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MessageHandler {

    MessageListener.Mode value() default MessageListener.Mode.TOPIC;

}
