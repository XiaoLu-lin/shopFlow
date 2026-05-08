package org.ysling.shopflow.admin.model.user.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员批量注销结果
 * @author Ysling
 */
@Data
public class UserBatchDeleteResult implements Serializable {

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;
}
