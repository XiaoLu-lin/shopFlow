package org.ysling.litemall.admin.model.user.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import java.io.Serializable;

/**
 * 交易记录列表查询
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DealingSlipListBody extends PageBody implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 交易类型
     */
    private Short dealType;

    /**
     * 交易状态
     */
    private Boolean status;

}
