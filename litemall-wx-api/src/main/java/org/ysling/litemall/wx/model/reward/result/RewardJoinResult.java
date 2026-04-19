package org.ysling.litemall.wx.model.reward.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallReward;
import org.ysling.litemall.db.domain.LitemallRewardTask;

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
    private LitemallReward reward;

    /**
     * 赏金规则信息
     */
    private LitemallRewardTask rewardTask;

}
