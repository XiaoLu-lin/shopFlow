package org.ysling.shopflow.core.system.annotation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.ysling.shopflow.core.system.annotation.SystemEnums;

/**
 * @author Ysling
 */
@Aspect
@Component
public class SystemEnumsAspect {

    @Around("@annotation(tenant)")
    public Object tenantAspect(ProceedingJoinPoint joinPoint, SystemEnums tenant) throws Throwable{
        return joinPoint.proceed();
    }

}
