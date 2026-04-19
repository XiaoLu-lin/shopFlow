package org.ysling.litemall.core.transaction.aspect;

/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.transaction.context.AsyncTransactionalContextHolder;
import org.ysling.litemall.core.transaction.annotation.MainAsyncTransactional;

/**
 * @author Ysling
 */
@Slf4j
@Aspect
@Component
public class MainAsyncTransactionalAspect {

    @Around("@annotation(transactional)")
    public Object transactionalAspect(ProceedingJoinPoint joinPoint, MainAsyncTransactional transactional) throws Throwable{
        Object result;
        try {
            //执行方法
            result = joinPoint.proceed();
            //等待子线程执行
            AsyncTransactionalContextHolder.initTask();
        }catch (Exception e){
            //重新抛出异常
            throw new RuntimeException(e.getMessage());
        }finally {
            //清除线程缓存
            AsyncTransactionalContextHolder.remove();
        }
        return result;
    }

}
