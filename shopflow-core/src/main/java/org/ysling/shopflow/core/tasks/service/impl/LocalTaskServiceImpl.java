package org.ysling.shopflow.core.tasks.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ysling.shopflow.core.handler.ActionLogHandler;
import org.ysling.shopflow.core.tasks.service.TaskRunnable;
import org.ysling.shopflow.core.tasks.service.TaskService;
import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 本地延时队列
 */
@Slf4j
@Component
public class LocalTaskServiceImpl implements TaskService {


    @Autowired
    private ScheduledExecutorService executorService;
    /**延时队列*/
    private final DelayQueue<TaskRunnable> delayQueue = new DelayQueue<>();


    /**
     * 创建任务执行器，阻塞执行
     */
    @PostConstruct
    private void init() {
        log.info("初始化 -> [任务执行器-LocalTaskServiceImpl]");
        executorService.scheduleAtFixedRate(() -> {
            try {
                // 获取队列
                TaskRunnable task = delayQueue.take();
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
        if(delayQueue.contains(task)){
            return;
        }
        delayQueue.add(task);
    }

    @Override
    public void updateTask(TaskRunnable task) {
        removeTask(task);
        addTask(task);
    }

    @Override
    public void removeTask(TaskRunnable task){
        if (!delayQueue.remove(task)){
            log.error(String.format("removeTask失败--{%s}",task.getId()));
        }
    }


}
