package org.ysling.litemall.core.utils;


import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.concurrent.Callable;

/**
 * 事务工具类
 * @author Ysling
 */
public class TransactionUtils {

    /**
     * 在当前事务执行完毕，提交持久化到数据库之后，执行回调方法
     * 执行回调方法，常用于回调与当前主线事务无关的其他业务处理
     * @param callable 回调业务接口方法
     */
    public static void afterCommit(Callable<?> callable) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 执行回调方法，执行事务提交之后其他业务
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
