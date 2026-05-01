package org.ysling.shopflow.wx.model.reward.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowRewardTask;
import org.ysling.shopflow.db.entity.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Ysling
 */
@Data
public class RewardListInfo implements Serializable {

    /**
     * 赏金规则
     */
    private ShopflowRewardTask rewardTask;

    /**
     * 完成百分比
     */
    private String percentage;

    /**
     * 参与数量
     */
    private Long total;

    /**
     * 参与用户
     */
    private ArrayList<UserInfo> joiners;

}
