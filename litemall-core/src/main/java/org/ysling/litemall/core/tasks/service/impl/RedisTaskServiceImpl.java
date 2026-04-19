package org.ysling.litemall.core.tasks.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.tasks.service.TaskRunnable;
import org.ysling.litemall.core.tasks.service.TaskService;
import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * redis延时队列实现
 */
@Slf4j
@Primary
@Component
public class RedisTaskServiceImpl implements TaskService {


    @Autowired
    private ScheduledExecutorService executorService;
    /**延时队列*/
    private final RDelayedQueue<TaskRunnable> delayedQueue;
    /**延时队列*/
    private final RBlockingQueue<TaskRunnable> blockingQueue;


    /**注入RedissonClient**/
    public RedisTaskServiceImpl(@Qualifier("redisson") RedissonClient redissonClient){
        this.blockingQueue = redissonClient.getBlockingQueue("TASK-SERVICE");
        this.delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
    }

    /**
     * 初始化任务执行器
     */
    @PostConstruct
    private void init() {
        log.info("初始化 -> [任务执行器-RedisTaskServiceImpl]");
        executorService.scheduleAtFixedRate(() -> {
            try {
                // 获取任务，没有任务阻塞
                TaskRunnable task = blockingQueue.take();
                // 队列运行
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
                //记录异常操作
                ActionLogHandler.logOtherFail("系统处理延时任务", e);
            }
            // 第一次执行的时间为5秒，然后每隔1秒执行一次
        }, 1, 1, TimeUnit.MILLISECONDS);
    }


    @Override
    public void addTask(TaskRunnable task){
        for (TaskRunnable runnable :delayedQueue) {
            if (runnable.equals(task)){
                return;
            }
        }
        delayedQueue.offer(task, task.getSeconds(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateTask(TaskRunnable task) {
        removeTask(task);
        addTask(task);
    }

    @Override
    public void removeTask(TaskRunnable task){
        if (!delayedQueue.removeIf(runnable -> runnable.equals(task))){
            log.error(String.format("removeTask失败--{%s}",task.getId()));
        }
    }


}
