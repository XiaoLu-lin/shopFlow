package org.ysling.litemall.core.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.service.IGoodsService;
import org.ysling.litemall.db.service.IRewardService;
import org.ysling.litemall.db.service.IRewardTaskService;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.enums.RewardStatus;
import org.ysling.litemall.db.enums.RewardTaskStatus;
import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Service
public class RewardCoreService {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IRewardTaskService rewardTaskService;
    @Autowired
    private IRewardService rewardService;
    @Autowired
    private CommonService commonService;


    /**
     * 修改赏金状态和订单状态
     */
    public void updateRewardStatus(LitemallReward reward){
        reward.setStatus(RewardStatus.STATUS_SUCCEED.getStatus());
        if (rewardService.updateVersionSelective(reward) <= 0){
            throw new RuntimeException("赏金更新数据失败");
        }
        LitemallRewardTask rewardTask = rewardTaskService.findById(reward.getTaskId());
        if (commonService.countReward(reward.getTaskId()) >= rewardTask.getRewardMember()){
            rewardTask.setStatus(RewardTaskStatus.TASK_STATUS_SUCCEED.getStatus());
        }
        BigDecimal decimal = BigDecimal.valueOf(rewardTask.getJoinersMember()).add(BigDecimal.ONE);
        rewardTask.setJoinersMember(decimal.intValue());
        if (rewardTaskService.updateVersionSelective(rewardTask) <= 0){
            throw new RuntimeException("赏金更新数据已失败");
        }
    }



    /**
     * 验证赏金是否有效
     * @return null
     */
    public Object isReward(String rewardTaskId){
        if (rewardTaskId == null) {
            return ResponseUtil.badArgumentValue();
        }
        LitemallRewardTask rewardTask = rewardTaskService.findById(rewardTaskId);
        if (rewardTask == null) {
            return ResponseUtil.fail(800,"活动不存在");
        }

        if (!RewardTaskStatus.TASK_STATUS_ON.getStatus().equals(rewardTask.getStatus())){
            return ResponseUtil.fail(800,"活动已下线");
        }

        if (commonService.countReward(rewardTask.getId()) >= rewardTask.getRewardMember()){
            return ResponseUtil.fail(800,"活动已结束");
        }
        LitemallGoods goods = goodsService.findById(rewardTask.getGoodsId());
        if (goods == null) {
            return ResponseUtil.fail(800,"商品不存在");
        }

        return null;
    }


    /**
     * 添加赏金记录
     * @param rewardLinkId 赏金参与id
     * @param userId 用户id
     * @param order 订单
     */
    public void addReward(String rewardLinkId, String userId , LitemallOrder order){
        //添加赏金活动
        if (rewardLinkId != null && !"0".equals(rewardLinkId)){
            LitemallReward reward = rewardService.findById(rewardLinkId);
            LitemallRewardTask rewardTask = rewardTaskService.findById(reward.getTaskId());
            LitemallReward newReward = new LitemallReward();
            newReward.setUserId(userId);
            newReward.setOrderId(order.getId());
            newReward.setRewardId(reward.getId());
            newReward.setTaskId(rewardTask.getId());
            newReward.setAward(rewardTask.getAward());
            newReward.setCreatorUserId(reward.getUserId());
            newReward.setStatus(RewardStatus.STATUS_NONE.getStatus());
            rewardService.add(newReward);
        }
    }
}
