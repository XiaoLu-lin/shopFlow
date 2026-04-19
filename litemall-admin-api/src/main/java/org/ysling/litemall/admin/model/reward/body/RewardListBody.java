package org.ysling.litemall.admin.model.reward.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

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
