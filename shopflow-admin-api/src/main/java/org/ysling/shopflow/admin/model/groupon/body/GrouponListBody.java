package org.ysling.shopflow.admin.model.groupon.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

/**
 * 团购参与列表列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GrouponListBody extends PageBody {

    /**
     * 团购状态
     */
    private Short status;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 团购规则ID
     */
    private String ruleId;
    /**
     * 团购ID
     */
    private String grouponId;

}
