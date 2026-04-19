package org.ysling.litemall.core.baidu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.ysling.litemall.core.baidu.service.BaibuAipSecCheckService;

/**
 * post请求参数审核
 * @author Ysling
 */
@Slf4j
@Aspect
@Component
public class TextCheckAspect {


    @Around("@annotation(mapping)")
    public Object tenantAspect(ProceedingJoinPoint joinPoint, PostMapping mapping) throws Throwable{
        // 请求方法参数值
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            StringBuilder params = new StringBuilder();
            for (Object arg : args) {
                if (arg != null){
                    params.append(arg);
                }
            }
            BaibuAipSecCheckService.textSecCheck(params.toString());
        }
        return joinPoint.proceed();
    }


}