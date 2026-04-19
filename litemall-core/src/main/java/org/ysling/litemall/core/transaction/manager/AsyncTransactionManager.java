package org.ysling.litemall.core.transaction.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.ysling.litemall.core.transaction.model.TransactionResource;
import org.ysling.litemall.core.utils.Inheritable.InheritableRunnable;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 多线程事务一致性管理 <br>
 * 声明式事务管理无法完成,此时我们只能采用初期的编程式事务管理才行
 * @author Ysling
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncTransactionManager {

    /** 如果是多数据源的情况下,需要指定具体是哪一个数据源 */
    private final DataSource dataSource;

    /**
     * 执行的是无返回值的任务
     * 异步执行任务需要用到的线程池,考虑到线程池需要隔离
     * @param tasks 异步执行的任务列表
     */
    public void runAsyncButWaitUntilAllDown(List<Runnable> tasks) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 用于判断主线程是否提交
        CountDownLatch mainLatch = new CountDownLatch(1);
        //是否发生了异常
        AtomicBoolean isError = new AtomicBoolean();
        //事务管理器
        DataSourceTransactionManager transactionManager = getTransactionManager();
        //异常信息
        List<Exception> exceptions = new ArrayList<>(tasks.size());
        //多线程任务
        List<CompletableFuture<?>> taskFutureList = new ArrayList<>(tasks.size());
        //事务的一些状态信息，如是否是一个新的事务、是否已被标记为回滚
        List<TransactionStatus> transactionStatusList = new ArrayList<>(tasks.size());
        //事务资源
        List<TransactionResource> transactionResources = new ArrayList<>(tasks.size());
        //执行任务
        tasks.forEach(task -> taskFutureList.add(CompletableFuture.runAsync(new InheritableRunnable() {
            @Override
            public void runTask() {
                try{
                    //1.开启新事务
                    transactionStatusList.add(openNewTransaction(transactionManager));
                    //2.copy事务资源
                    transactionResources.add(TransactionResource.copyTransactionResource());
                    //3.异步任务执行
                    task.run();
                }catch (Exception e){
                    //其中某个异步任务执行出现了异常,进行标记
                    isError.set(Boolean.TRUE);
                    //保存异常信息
                    exceptions.add(e);
                    //其他任务还没执行的不需要执行了
                    taskFutureList.forEach(completableFuture -> completableFuture.cancel(Boolean.TRUE));
                }
            }
        })));

        try {
            //阻塞直到所有任务全部执行结束---如果有任务被取消,这里会抛出异常滴,需要捕获
            CompletableFuture.allOf(taskFutureList.toArray(new CompletableFuture[]{})).get();
        } catch (Throwable throwable) {
            for (Exception exception :exceptions) {
                throw new RuntimeException(exception.getMessage());
            }
            throw new RuntimeException(throwable.getMessage());
        } finally {
            //发生了异常则进行回滚操作,否则提交
            executorService.execute(()->{
                try {
                    mainLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(isError.get()){
                    log.error("发生异常,全部事务回滚");
                    for (int i = 0; i < transactionStatusList.size(); i++) {
                        transactionResources.get(i).autoWiredTransactionResource();
                        transactionManager.rollback(transactionStatusList.get(i));
                        transactionResources.get(i).removeTransactionResource();
                    }
                }else {
                    log.info("全部事务正常提交");
                    for (int i = 0; i < transactionStatusList.size(); i++) {
                        transactionResources.get(i).autoWiredTransactionResource();
                        transactionManager.commit(transactionStatusList.get(i));
                        transactionResources.get(i).removeTransactionResource();
                    }
                }
            });
        }

        mainLatch.countDown();
    }

    private TransactionStatus openNewTransaction(DataSourceTransactionManager transactionManager) {
        //JdbcTransactionManager根据TransactionDefinition信息来进行一些连接属性的设置
        //包括隔离级别和传播行为等
        DefaultTransactionDefinition transactionDef = new DefaultTransactionDefinition();
        //开启一个新事务---此时autocommit已经被设置为了false,并且当前没有事务,这里创建的是一个新事务
        transactionDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionManager.getTransaction(transactionDef);
    }

    private DataSourceTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

}

