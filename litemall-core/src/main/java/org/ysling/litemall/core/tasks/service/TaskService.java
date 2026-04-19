package org.ysling.litemall.core.tasks.service;
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

/**
 * 定时任务执行服务类
 * @author Ysling
 */
public interface TaskService {


    /**
     * 添加任务
     * @param task 任务
     */
    void addTask(TaskRunnable task);

    /**
     * 修改任务
     * @param task 任务
     */
    void updateTask(TaskRunnable task);

    /**
     * 删除任务
     * @param task 任务
     */
    void removeTask(TaskRunnable task);

}
