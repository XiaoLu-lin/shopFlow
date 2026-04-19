package org.ysling.litemall.core.redis.aspect;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.redis.annotation.RequestRateLimiter;
import org.ysling.litemall.core.utils.response.ResponseUtil;

import java.util.Objects;

/**
 * 接口限流注解实现
 * @author Ysling
 */
@Aspect
@Component
public class RequestRateLimitAspect {

    @Autowired
    private RedissonClient redisson;

    /**
     * KEY前缀
     */
    private static final String KEY_PREFIX = "RATE_LIMITER:";

    /**
     * 注解切面
     */
    @Around(value ="@annotation(requestRateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint, RequestRateLimiter requestRateLimiter) throws Throwable {
        // 限流拦截器
        RRateLimiter rRateLimiter = getRateLimiter(joinPoint, requestRateLimiter);
        if (rRateLimiter.tryAcquire(1)) {
            return joinPoint.proceed();
        } else {
            return ResponseUtil.fail(requestRateLimiter.errMsg());
        }
    }

    /**
     * 获取限流拦截器
     */
    private RRateLimiter getRateLimiter(ProceedingJoinPoint joinPoint, RequestRateLimiter requestRateLimiter){
        RRateLimiter rRateLimiter = redisson.getRateLimiter(StringUtils.isBlank(requestRateLimiter.key()) ? keyGenerator(joinPoint) : requestRateLimiter.key());
        // 设置限流
        if(rRateLimiter.isExists()) {
            RateLimiterConfig rateLimiterConfig = rRateLimiter.getConfig();
            // 判断配置是否更新，如果更新，重新加载限流器配置
            if (!Objects.equals(requestRateLimiter.rate(), rateLimiterConfig.getRate())
                    || !Objects.equals(requestRateLimiter.timeUnit().toMillis(requestRateLimiter.rateInterval()), rateLimiterConfig.getRateInterval())
                    || !Objects.equals(requestRateLimiter.type(), rateLimiterConfig.getRateType())) {
                rRateLimiter.delete();
                rRateLimiter.trySetRate(requestRateLimiter.type(), requestRateLimiter.rate(), requestRateLimiter.rateInterval(), requestRateLimiter.timeUnit());
            }
        } else {
            rRateLimiter.trySetRate(requestRateLimiter.type(), requestRateLimiter.rate(), requestRateLimiter.rateInterval(), requestRateLimiter.timeUnit());
        }
        return rRateLimiter;
    }

    /**
     * 限流key生成策略 RATE_LIMITER + 类名+方法名+参数列表哈希值
     */
    private String keyGenerator(ProceedingJoinPoint joinPoint) {
        String target = joinPoint.getTarget().getClass().getName();
        String[] split = target.split("\\.");
        String className = joinPoint.getSignature().getName();
        StringBuilder defaultKey = new StringBuilder();
        defaultKey.append(KEY_PREFIX);
        defaultKey.append(split[split.length - 1]).append(":");
        defaultKey.append(className).append(":");
        Object[] params = joinPoint.getArgs();
        for (Object obj : params) {
            // 由于参数可能不同, hashCode肯定不一样, 缓存的key也需要不一样
            defaultKey.append(JSON.toJSONString(obj).hashCode());
        }
        return defaultKey.toString();
    }

}
