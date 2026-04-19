package org.ysling.litemall.core.transaction.context;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 多租户上下文工具类
 * @author Ysling
 */
@Slf4j
public class AsyncTransactionalContextHolder {

    private static final ThreadLocal<List<Runnable>> THREAD_TASKS = new ThreadLocal<>();

    private static final ThreadLocal<List<Runnable>> THREAD_FINISH_TASKS = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> THREAD_IS_ERROR = new ThreadLocal<>();

    private static final ThreadLocal<String> THREAD_ERR_MSG = new ThreadLocal<>();

    /**
     * 处理异步执行的任务
     */
    public static void initTask() {
        startTask(THREAD_TASKS.get());
        startTask(THREAD_FINISH_TASKS.get());
    }

    private static void startTask(List<Runnable> taskList){
        if (taskList != null) {
            //多线程任务
            List<CompletableFuture<?>> taskFutureList = new ArrayList<>(taskList.size());
            //执行任务
            taskList.forEach(task -> taskFutureList.add(CompletableFuture.runAsync(task)));
            //所有任务执行完后执行回调
            try {
                CompletableFuture.allOf(taskFutureList.toArray(new CompletableFuture[]{})).get();
            }catch (Throwable throwable){
                throwable.printStackTrace();
                throw new RuntimeException(throwable.getMessage());
            }
        }
    }

    public static void setThreadFinishTasks(Runnable futureTask){
        List<Runnable> futureTasks = THREAD_FINISH_TASKS.get();
        if (futureTasks == null) {
            futureTasks = Collections.synchronizedList(new ArrayList<>());
            futureTasks.add(futureTask);
            THREAD_FINISH_TASKS.set(futureTasks);
        }else {
            THREAD_FINISH_TASKS.get().add(futureTask);
        }
    }

    public static List<Runnable> getThreadFinishTasks(){
        return THREAD_FINISH_TASKS.get() == null ? new ArrayList<>() : THREAD_FINISH_TASKS.get();
    }

    public static void setThreadTasks(Runnable futureTask){
        List<Runnable> futureTasks = THREAD_TASKS.get();
        if (futureTasks == null) {
            futureTasks = Collections.synchronizedList(new ArrayList<>());
            futureTasks.add(futureTask);
            THREAD_TASKS.set(futureTasks);
        }else {
            THREAD_TASKS.get().add(futureTask);
        }
    }

    public static List<Runnable> getThreadTasks(){
        return THREAD_TASKS.get() == null ? new ArrayList<>() : THREAD_TASKS.get();
    }

    public static void setThreadIsError(Boolean error){
        if (THREAD_IS_ERROR.get() == null){
            THREAD_IS_ERROR.set(error);
        }
    }

    public static Boolean getThreadIsError(){
        return THREAD_IS_ERROR.get() != null && THREAD_IS_ERROR.get();
    }

    public static void setThreadErrMsg(String errMsg){
        if (THREAD_ERR_MSG.get() == null){
            THREAD_ERR_MSG.set(errMsg);
        }
    }

    public static String getThreadErrMsg(){
        return THREAD_ERR_MSG.get();
    }

    public static void remove(){
        THREAD_ERR_MSG.remove();
        THREAD_IS_ERROR.remove();
        THREAD_TASKS.remove();
        THREAD_FINISH_TASKS.remove();
    }

}
