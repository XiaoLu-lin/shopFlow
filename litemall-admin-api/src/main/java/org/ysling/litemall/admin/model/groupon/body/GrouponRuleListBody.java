package org.ysling.litemall.admin.model.groupon.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 团购规则列表列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GrouponRuleListBody extends PageBody {

    /**
     * 商品ID
     */
    private String goodsId;

}
