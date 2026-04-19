package org.ysling.litemall.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.ip.IpUtil;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义日志处理
 * 过滤器Filter可以拿到原始的HTTP请求和响应的信息，但是拿不到你真正处理请求方法的信息，也就是方法的信息
 * 拦截顺序：filter—>Interceptor-->ControllerAdvice-->@Aspect -->Controller
 * @author Ysling
 */
@Slf4j
@Order(2)
@WebFilter(urlPatterns = {"/wx/*" , "/admin/*"}, filterName = "requestLogPrintFilter")
public class RequestLogPrintFilter extends CommonsRequestLoggingFilter {

    /**
     * 开始时间
     */
    private long start = 0;

    /**
     * 重写父类方法：请求前调用逻辑
     */
    @Override
    protected void beforeRequest(@NotNull HttpServletRequest request, @NotNull String message) {
        start = System.currentTimeMillis();
    }

    /**
     * 重写父类方法：请求后调用逻辑
     */
    @Override
    protected void afterRequest(@NotNull HttpServletRequest request, @NotNull String message) {
        long end = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder();
        builder.append("{当前租户:").append(TenantContextHolder.returnTenantId(request)).append("} ");
        builder.append("{请求地址:").append(request.getMethod()).append("  ");
        builder.append(IpUtil.getIpAddr(request));
        builder.append(":");
        builder.append(request.getLocalPort());
        builder.append(request.getRequestURI());
        if (request.getQueryString() != null){
            builder.append("?");
            builder.append(request.getQueryString());
        }
        builder.append("  }-");
        builder.append("{执行时间:").append(end - start).append(" ms").append("}");
        log.info(builder.toString());
        //记录大于5秒的请求
        if (end - start > 5000){
            ActionLogHandler.logOtherFail("接口响应大于5秒" , builder.toString());
        }
    }

}
