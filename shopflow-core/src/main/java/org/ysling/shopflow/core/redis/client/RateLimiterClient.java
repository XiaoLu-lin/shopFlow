package org.ysling.shopflow.core.redis.client;


import cn.hutool.crypto.digest.DigestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ysling.shopflow.core.utils.JacksonUtil;

import java.util.Objects;

/**
 * 限流工具类
 * @author Ysling
 */
@Component
public class RateLimiterClient {


    @Autowired
    private RedissonClient redisson;

    /**
     * 限流速率，生成令牌数量
     */
    private static final long RATE = 1;

    /**
     * 限流速率 多少秒生成一次令牌（单位秒）
     */
    private static final long RATE_INTERVAL = 3;

    /**
     * 限流模式,默认单机
     */
    private final RateType type = RateType.PER_CLIENT;

    /**
     * 限流速率单位 默认单位秒
     */
    private final RateIntervalUnit timeUnit = RateIntervalUnit.SECONDS;


    /**
     * 获取限流拦截器
     * @param name          拦截器名称
     */
    public RRateLimiter getRateLimiter(String name){
        return getRateLimiter(name, RATE, RATE_INTERVAL, timeUnit, type);
    }

    /**
     * 获取限流拦截器
     * @param name          拦截器名称
     * @param rate          限流速率，生成令牌数量
     * @param rateInterval  限流速率 多少秒生成一次令牌（单位秒）
     */
    public RRateLimiter getRateLimiter(String name, long rate, long rateInterval){
        return getRateLimiter(name, rate, rateInterval, timeUnit, type);
    }

    /**
     * 获取限流拦截器
     * @param name          拦截器名称
     * @param rate          限流速率，生成令牌数量
     * @param rateInterval  限流速率 多少秒生成一次令牌（单位秒）
     */
    public RRateLimiter getRateLimiter(String name, long rate, long rateInterval, RateIntervalUnit timeUnit){
        return getRateLimiter(name, rate, rateInterval, timeUnit, type);
    }

    /**
     * 获取限流拦截器
     * @param name          拦截器名称
     * @param rate          限流速率，生成令牌数量
     * @param rateInterval  限流速率 多少秒生成一次令牌（单位秒）
     * @param timeUnit      限流速率单位 默认单位秒
     */
    public RRateLimiter getRateLimiter(String name, long rate, long rateInterval, RateIntervalUnit timeUnit, RateType type){
        RRateLimiter rRateLimiter = redisson.getRateLimiter(name);
        // 设置限流
        if(rRateLimiter.isExists()) {
            RateLimiterConfig config = rRateLimiter.getConfig();
            // 判断配置是否更新，如果更新，重新加载限流器配置
            if (!Objects.equals(rate, config.getRate())
                    || !Objects.equals(timeUnit.toMillis(rateInterval), config.getRateInterval())
                    || !Objects.equals(type, config.getRateType())) {
                rRateLimiter.setRate(type, rate, rateInterval, timeUnit);
            }
        } else {
            rRateLimiter.trySetRate(type, rate, rateInterval, timeUnit);
        }
        return rRateLimiter;
    }


    /**
     * 限流key生成策略 RATE_LIMITER + 类名+方法名+参数列表哈希值
     * @param keyPrefix     key前缀
     * @param joinPoint     切面代理
     */
    public String keyGenerator(String keyPrefix, ProceedingJoinPoint joinPoint) {
        String target = joinPoint.getTarget().getClass().getName();
        String[] split = target.split("\\.");
        String className = joinPoint.getSignature().getName();
        StringBuilder defaultKey = new StringBuilder();
        defaultKey.append(keyPrefix);
        defaultKey.append(split[split.length - 1]).append(":");
        defaultKey.append(className).append(":");
        Object[] params = joinPoint.getArgs();
        String jsonStr = JacksonUtil.toJson(params);
        defaultKey.append(DigestUtil.md5Hex16(jsonStr));
        return defaultKey.toString();
    }


}
