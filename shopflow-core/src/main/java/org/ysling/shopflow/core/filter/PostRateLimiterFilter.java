package org.ysling.shopflow.core.filter;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RRateLimiter;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.ysling.shopflow.core.baidu.service.BaibuAipSecCheckService;
import org.ysling.shopflow.core.redis.client.RateLimiterClient;
import org.ysling.shopflow.core.utils.http.CustomRequestWrapper;
import org.ysling.shopflow.core.utils.http.GlobalWebUtil;
import org.ysling.shopflow.core.utils.ip.IpUtil;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

/**
 * 接口防重复提交限流器
 */
@Slf4j
@Order(1)
@WebFilter(urlPatterns = {"/wx/*" , "/admin/*"}, filterName = "postRateLimiterFilter")
public class PostRateLimiterFilter extends OncePerRequestFilter {

    @Resource
    private RateLimiterClient limiterClient;

    /**
     * KEY前缀
     */
    private static final String KEY_PREFIX = "POST_RATE_LIMITER_FILTER:";

    /**
     * 限流拦截器
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //判断类型仅拦截application/json
        String method = request.getMethod();
        String contentType = request.getContentType();
        if (contentType != null && Objects.equals(method, "POST") && contentType.contains("application/json")) {
            // 获取请求的参数
            CustomRequestWrapper wrapper = new CustomRequestWrapper(request);
            String requestBody = GlobalWebUtil.getJsonBody(wrapper);
            try {
                //文本审核
                BaibuAipSecCheckService.textSecCheck(requestBody);
            }catch (Exception e){
                GlobalWebUtil.sendMessage(response, "请求参数：" + e.getMessage());
                return;
            }
            String ip = IpUtil.getIpAddr(request);
            String name = KEY_PREFIX +ip + ":" + request.getRequestURI() + ":" + DigestUtil.md5Hex16(requestBody);;
            RRateLimiter rRateLimiter = limiterClient.getRateLimiter(name);
            if (rRateLimiter.tryAcquire()) {
                try {
                    // 继续处理其他过滤器或Servlet
                    filterChain.doFilter(wrapper, response);
                }finally {
                    //请求处理完成删除限制器
                    rRateLimiter.expire(Instant.EPOCH);
                }
            } else {
                GlobalWebUtil.sendMessage(response,"你有一个相同请求已提交，请等待");
            }
        }else {
            // 继续处理其他过滤器或Servlet
            filterChain.doFilter(request, response);
        }
    }


}

