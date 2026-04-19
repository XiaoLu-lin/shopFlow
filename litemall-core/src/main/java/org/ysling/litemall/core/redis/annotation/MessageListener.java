package org.ysling.litemall.core.redis.annotation;

import java.lang.annotation.*;

/**
 * redis消息队列监听注解
 * 广播
 * //   @MessageListener(channel = "pubsub", mode = MessageListener.Mode.PUBSUB)
 *      public void testTopic3(Message<?> message) {
 *         log.info("topic3===> " + message);
 *      }
 * 订阅
 * //   @MessageListener(topic = "topic3", mode = MessageListener.Mode.TOPIC)
 *      public void testPubsub2(Message<?> message) {
 *         log.info("pubsub2===> " + message);
 *     }
 * @author Ysling
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageListener {

    /**
     * 名称
     * @return default ""
     */
    String value() default "";

    /**
     * 主题名称
     * @return default ""
     */
    String topic() default "";

    /**
     * 广播名称
     * @return default ""
     */
    String channel() default "";

    /**
     * 是否可以重复消费
     * @return default false
     */
    boolean reuse() default false;

    /**
     * 消息类型
     * @return default Mode.TOPIC
     */
    Mode mode() default Mode.TOPIC;

    /**
     * 类型
     */
    enum Mode {
        /**
         * topic 模式，主题订阅
         */
        TOPIC(),
        /**
         * pub/sub 模式 订阅发布 广播
         */
        PUBSUB(),
    }
}
