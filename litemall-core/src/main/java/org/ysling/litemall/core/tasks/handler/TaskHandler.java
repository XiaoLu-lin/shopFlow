package org.ysling.litemall.core.tasks.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import java.io.Serializable;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
public abstract class TaskHandler implements Serializable {

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 租户ID列表
     */
    private List<String> tenantIds;
    /**
     * 搞个代理方法，这个方法中处理业务逻辑
     */
    public abstract Integer runTask();

    /**
     * 执行任务
     */
    public void run() {
        for (String tenantId : tenantIds) {
            TenantContextHolder.setLocalTenantId(tenantId);
            log.info("租户[{}] ->系统定时任务 -> [开始检查-{}]", tenantId, taskName);
            Integer taskSize = this.runTask();
            log.info("租户[{}] ->系统定时任务 -> [结束检查-{}][检查结果=数量{}]", tenantId, taskName, taskSize);
            TenantContextHolder.removeLocalTenantId();
        }
    }


}
