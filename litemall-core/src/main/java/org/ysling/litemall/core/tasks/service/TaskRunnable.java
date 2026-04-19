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

import com.baomidou.dynamic.datasource.tx.LocalTxUtil;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 自定义任务线程
 * @author Ysling
 */
@Slf4j
@Data
@AllArgsConstructor
public abstract class TaskRunnable implements Runnable, Serializable, Delayed {

    /** 任务ID */
    private final String id;
    /** 延迟时间 */
    private final long seconds;
    /** 租户ID */
    private final String tenantId;
    /** 任务名称 */
    private final String taskName;

    /** 搞个代理方法，这个方法中处理业务逻辑 */
    public abstract void runTask();

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.seconds - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return Ints.saturatedCast(this.seconds - ((TaskRunnable) o).seconds);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof TaskRunnable)) {
            return false;
        }
        TaskRunnable t = (TaskRunnable)o;
        return this.id.equals(t.getId());
    }

    /**
     * 给业务方法添加事务与多租户
     */
    @Override
    public void run() {
        try {
            //添加当前租户
            TenantContextHolder.setLocalTenantId(this.tenantId);
            // 手动开启事务
            LocalTxUtil.startTransaction();
            // 执行代理任务
            log.info(String.format("系统处理延时任务 -> [开始-%s] [任务ID-%s]", taskName, id));
            runTask();
            log.info(String.format("系统处理延时任务 -> [结束-%s] [任务ID-%s]", taskName, id));
            // 手动提交事务
            LocalTxUtil.commit();
        } catch (Throwable e) {
            //手动回滚事务 最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
            LocalTxUtil.rollback();
            //事务回滚后继续抛出异常
            throw new RuntimeException(e);
        } finally {
            //清除租户信息
            TenantContextHolder.removeLocalTenantId();
            //添加操作日志
            ActionLogHandler.logOrderSucceed("系统处理延时任务", String.format("[%s] [任务ID-%s]", taskName, id));
        }
    }


}
