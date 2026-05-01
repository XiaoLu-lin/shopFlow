package org.ysling.shopflow.wx.model.reward.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowReward;
import org.ysling.shopflow.db.domain.ShopflowRewardTask;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class RewardJoinResult implements Serializable {

    /**
     * 商品ID
     */
    private String goodId;
    /**
     * 赏金参与信息
     */
    private ShopflowReward reward;

    /**
     * 赏金规则信息
     */
    private ShopflowRewardTask rewardTask;

}
