package org.ysling.shopflow.wx.service;
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
import org.ysling.shopflow.db.domain.ShopflowReward;
import org.ysling.shopflow.db.enums.RewardStatus;
import org.ysling.shopflow.db.service.impl.RewardServiceImpl;
import java.util.List;

/**
 * 赏金参与服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_reward")
public class WxRewardService extends RewardServiceImpl {


    @Cacheable(sync = true)
    public Integer countReward(String taskId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.TASK_ID , taskId);
        wrapper.eq(ShopflowReward.STATUS , RewardStatus.STATUS_SUCCEED.getStatus());
        return Math.toIntExact(count(wrapper));
    }
    
    @Cacheable(sync = true)
    public Integer countAndReward(String userId, String taskId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.USER_ID , userId);
        wrapper.eq(ShopflowReward.TASK_ID , taskId);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 获取分享者记录
     */
    @Cacheable(sync = true)
    public ShopflowReward findSharer(String userId, String taskId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.USER_ID , userId);
        wrapper.eq(ShopflowReward.TASK_ID , taskId);
        wrapper.eq(ShopflowReward.REWARD_ID , "0");
        return getOne(wrapper);
    }

    /**
     * 获取用户所有分享记录
     */
    @Cacheable(sync = true)
    public List<ShopflowReward> querySharerUserId(String userId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.USER_ID , userId);
        wrapper.eq(ShopflowReward.STATUS , RewardStatus.STATUS_SUCCEED.getStatus());
        return queryAll(wrapper);
    }

    /**
     * 获取某个赏金活动参与的记录
     */
    @Cacheable(sync = true)
    public List<ShopflowReward> queryJoinRecord(String rewardId) {
        QueryWrapper<ShopflowReward> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowReward.REWARD_ID , rewardId);
        wrapper.eq(ShopflowReward.STATUS , RewardStatus.STATUS_SUCCEED.getStatus());
        wrapper.orderByDesc(ShopflowReward.ADD_TIME);
        return queryAll(wrapper);
    }

}
