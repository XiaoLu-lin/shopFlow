package org.ysling.litemall.admin.model.reward.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 赏金参与列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RewardJoinBody extends PageBody {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 赏金规则ID
     */
    private String taskId;
    /**
     * 赏金参与ID
     */
    private String rewardId;
    /**
     * 赏金状态
     */
    private Short status;

}
