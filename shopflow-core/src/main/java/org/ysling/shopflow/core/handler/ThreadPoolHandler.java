package org.ysling.shopflow.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.ysling.shopflow.core.tenant.handler.TenantContextHolder;

/**
 * 自带事务处理的异步线程池
 * @author Ysling
 */
@Slf4j
@Component
public class ThreadPoolHandler {

    @Autowired
    private TransactionDefinition transactionDefinition;
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 异步重试任务
     * @param task 任务
     */
    @Async
    @Retryable(value = Throwable.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 1.5))
    public void start(Runnable task) {
        task.run();
    }

    /**
     * 异步重试任务
     * @param task 任务
     */
    @Async
    @Retryable(value = Throwable.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 1.5))
    public void start(String tenantId, Runnable task) {
        try {
            //切换租户
            TenantContextHolder.setLocalTenantId(tenantId);
            //执行任务
            task.run();
        }finally {
            //清除线程缓存
            TenantContextHolder.removeLocalTenantId();
        }
    }

    /**
     * 带事务处理异步任务
     * @param task 异步任务
     */
    @Async
    @Retryable(value = Throwable.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 1.5))
    public void executeTask(Runnable task) {
        //手动开启事务
        TransactionStatus transactionStatus = null;
        try {
            // 手动开启事务
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
            // 队列运行
            task.run();
            // 手动提交事务
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            //手动回滚事务 最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
            if (transactionStatus != null){
                dataSourceTransactionManager.rollback(transactionStatus);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 带事务处理异步任务
     * @param task 异步任务
     */
    @Async
    @Retryable(value = Throwable.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 1.5))
    public void executeTask(String tenantId, Runnable task) {
        //手动开启事务
        TransactionStatus transactionStatus = null;
        try {
            //切换租户
            TenantContextHolder.setLocalTenantId(tenantId);
            // 手动开启事务
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
            // 队列运行
            task.run();
            // 手动提交事务
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            //手动回滚事务 最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
            if (transactionStatus != null){
                dataSourceTransactionManager.rollback(transactionStatus);
            }
            throw new RuntimeException(e);
        }finally {
            //清除线程缓存
            TenantContextHolder.removeLocalTenantId();
        }
    }

    /**
     * 重试失败回调
     */
    @Recover
    public void recover(Throwable e) {
        //打印失败日志
        e.printStackTrace();
        //清除线程缓存
        TenantContextHolder.removeLocalTenantId();
        //记录异常操作
        ActionLogHandler.logOtherFail("任务重试执行失败", e);
    }

}