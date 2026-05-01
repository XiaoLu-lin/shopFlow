package org.ysling.shopflow.admin.model.reward.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

/**
 * 赏金列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RewardListBody extends PageBody {

    /**
     * 商品ID
     */
    private String goodsId;

}
