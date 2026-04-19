package org.ysling.litemall.core.redis.annotation;

import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * redisson限流器
 * // 每两秒生成一个限流令牌
 * // @RequestRateLimiter(key = "test", rate = 1, rateInterval = 2, timeUnit = RateIntervalUnit.SECONDS)
 * @author Ysling
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestRateLimiter {

    /**
     * 限流的key
     */
    String key() default "";

    /**
     * 限流模式,默认单机
     */
    RateType type() default RateType.PER_CLIENT;

    /**
     * 限流速率，生成令牌数量
     */
    long rate() default 10;

    /**
     * 限流速率 多少秒生成一次令牌
     */
    long rateInterval() default 1000;

    /**
     * 限流速率单位 默认单位毫秒
     */
    RateIntervalUnit timeUnit() default RateIntervalUnit.MILLISECONDS;

    /**
     * 提示信息
     */
    String errMsg() default "系统繁忙请稍后重试";

}
