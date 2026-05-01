package org.ysling.shopflow.admin.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
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
import org.ysling.shopflow.admin.model.reward.body.RewardJoinBody;
import org.ysling.shopflow.db.domain.ShopflowReward;
import org.ysling.shopflow.db.service.impl.RewardServiceImpl;
import java.util.List;

/**
 * 赏金服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_reward")
public class AdminRewardService extends RewardServiceImpl {

    
    @Cacheable(sync = true)
    public List<ShopflowReward> querySelective(RewardJoinBody body) {
        QueryWrapper<ShopflowReward> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(ShopflowReward.USER_ID , body.getUserId());
        }
        if(body.getTaskId() != null){
            wrapper.eq(ShopflowReward.TASK_ID , body.getTaskId());
        }
        if (body.getStatus() != null) {
            wrapper.eq(ShopflowReward.STATUS , body.getStatus());
        }
        if(body.getRewardId() != null){
            wrapper.eq(ShopflowReward.REWARD_ID , body.getRewardId());
        }
        return queryAll(wrapper);
    }


}
