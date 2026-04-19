package org.ysling.litemall.core.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 子线程任务执行返回实体类
 * @author Ysling
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTask {

    /**
     * 执行是否出现异常
     */
    private Boolean isError;

    /**
     * 异常信息
     */
    private String errMsg;

}
