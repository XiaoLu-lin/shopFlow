package org.ysling.litemall.core.transaction.interceptor;

import com.baomidou.dynamic.datasource.aop.DynamicLocalTransactionInterceptor;
import com.baomidou.dynamic.datasource.tx.LocalTxUtil;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.ysling.litemall.core.transaction.context.AsyncTransactionalContextHolder;

/**
 * 自定义动态数据源事务切面 -> 只有PostMapping请求才添加事务
 * @author Ysling
 */
public class LocalTransactionInterceptor extends DynamicLocalTransactionInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        PostMapping postMapping = AnnotationUtils.getAnnotation(invocation.getMethod(), PostMapping.class);
        if (postMapping != null){
            if (StringUtils.hasText(TransactionContext.getXID())) {
                return invocation.proceed();
            } else {
                boolean state = true;
                LocalTxUtil.startTransaction();
                Object o;
                try {
                    o = invocation.proceed();
                } catch (Exception var8) {
                    state = false;
                    throw var8;
                } finally {
                    if (state) {
                        LocalTxUtil.commit();
                    } else {
                        LocalTxUtil.rollback();
                    }
                    //清除线程缓存
                    AsyncTransactionalContextHolder.remove();
                }
                return o;
            }
        }else {
            return invocation.proceed();
        }
    }
}
