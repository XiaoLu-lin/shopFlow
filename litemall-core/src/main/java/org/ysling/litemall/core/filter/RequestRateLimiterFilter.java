package org.ysling.litemall.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RRateLimiter;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.redis.cache.RedisCacheService;
import org.ysling.litemall.core.redis.client.RateLimiterClient;
import org.ysling.litemall.core.utils.http.GlobalWebUtil;
import org.ysling.litemall.core.utils.ip.IpUtil;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 接口防刷限流器 每秒限10次
 * @author Ysling
 */
@Slf4j
@Order(0)
@WebFilter(urlPatterns = {"/wx/*" , "/admin/*"}, filterName = "requestRateLimiterFilter")
public class RequestRateLimiterFilter extends OncePerRequestFilter {

    @Resource
    private RateLimiterClient limiterClient;

    /**
     * 请求令牌桶 KEY前缀
     */
    private static final String REQUEST_RATE_PREFIX = "REQUEST_RATE_LIMITER_FILTER:";

    /**
     * 请求黑名单 KEY前缀
     */
    private static final String BLACK_LIST_PREFIX = "REQUEST_BLACK_LIST_FILTER:";

    /**
     * 访问超限禁用时间（单位分钟）
     */
    private static final long disabledTime = 60 * 24;

    /**
     * 限流速率，生成令牌数量
     */
    private static final long rate = 500;

    /**
     * 限流速率 多少秒生成一次令牌（单位秒）
     */
    private static final long rateInterval = 60;


    /**
     * 限流拦截器
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        String ip = IpUtil.getIpAddr(request);
        if (RedisCacheService.get(BLACK_LIST_PREFIX + ip) != null){
            GlobalWebUtil.sendMessage(response,"You've been blacklisted for visiting too often!");
            return;
        }
        String key = REQUEST_RATE_PREFIX + ip;
        RRateLimiter rateLimiter = limiterClient.getRateLimiter(key, rate, rateInterval);
        if (rateLimiter.tryAcquire()) {
            //责任链模式
            filterChain.doFilter(request, response);
        } else {
            log.warn("[{}] - [{}] 访问频率上限[{}]次/[{}]秒", url, ip, rate, rateInterval);
            RedisCacheService.put(BLACK_LIST_PREFIX + ip , "黑名单:"+ip , disabledTime , TimeUnit.MINUTES);
            GlobalWebUtil.sendMessage(response,"please wait some times!");
            ActionLogHandler.logOtherFail("限流拦截器IP封禁" , String.format("[%s] - [%s] 访问频率上限[%s]次/[%s]秒", url, ip, rate, rateInterval));
        }
    }

}

