package org.ysling.shopflow.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.reward.body.RewardListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowRewardTask;
import org.ysling.shopflow.db.service.impl.RewardTaskServiceImpl;
import java.math.BigDecimal;
import java.util.List;


/**
 * 赏金规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_reward_task")
public class AdminRewardTaskService extends RewardTaskServiceImpl {


    public Object validate(ShopflowRewardTask rewardTask) {
        String goodsId = rewardTask.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.badArgument();
        }

        Integer rewardMember = rewardTask.getRewardMember();
        if (rewardMember == null || rewardMember <= 0) {
            return ResponseUtil.badArgument();
        }

        BigDecimal award = rewardTask.getAward();
        if (award == null || award.intValue() < 1 || award.intValue() > 100){
            return ResponseUtil.fail("奖励1~100");
        }
        return null;
    }

    @Cacheable(sync = true)
    public ShopflowRewardTask findByGid(String goodsId) {
        QueryWrapper<ShopflowRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowRewardTask.GOODS_ID , goodsId);
        return getOne(wrapper);
    }

    @CacheEvict(allEntries = true)
    public void deleteByGid(String goodsId) {
        QueryWrapper<ShopflowRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowRewardTask.GOODS_ID , goodsId);
        remove(wrapper);
    }

    @Cacheable(sync = true)
    public Integer countByGoodsId(String goodsId) {
        QueryWrapper<ShopflowRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowRewardTask.GOODS_ID , goodsId);
        return Math.toIntExact(count(wrapper));
    }
    
    @Cacheable(sync = true)
    public List<ShopflowRewardTask> querySelective(RewardListBody body) {
        QueryWrapper<ShopflowRewardTask> wrapper = startPage(body);
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(ShopflowRewardTask.GOODS_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }

}
