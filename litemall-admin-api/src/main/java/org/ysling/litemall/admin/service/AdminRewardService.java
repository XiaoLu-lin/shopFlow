package org.ysling.litemall.admin.service;
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


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.admin.model.reward.body.RewardJoinBody;
import org.ysling.litemall.db.domain.LitemallReward;
import org.ysling.litemall.db.service.impl.RewardServiceImpl;
import java.util.List;

/**
 * 赏金服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_reward")
public class AdminRewardService extends RewardServiceImpl {

    
    @Cacheable(sync = true)
    public List<LitemallReward> querySelective(RewardJoinBody body) {
        QueryWrapper<LitemallReward> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(LitemallReward.USER_ID , body.getUserId());
        }
        if(body.getTaskId() != null){
            wrapper.eq(LitemallReward.TASK_ID , body.getTaskId());
        }
        if (body.getStatus() != null) {
            wrapper.eq(LitemallReward.STATUS , body.getStatus());
        }
        if(body.getRewardId() != null){
            wrapper.eq(LitemallReward.REWARD_ID , body.getRewardId());
        }
        return queryAll(wrapper);
    }


}
