package org.ysling.litemall.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.reward.body.RewardListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallRewardTask;
import org.ysling.litemall.db.service.impl.RewardTaskServiceImpl;
import java.math.BigDecimal;
import java.util.List;


/**
 * 赏金规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_reward_task")
public class AdminRewardTaskService extends RewardTaskServiceImpl {


    public Object validate(LitemallRewardTask rewardTask) {
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
    public LitemallRewardTask findByGid(String goodsId) {
        QueryWrapper<LitemallRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        return getOne(wrapper);
    }

    @CacheEvict(allEntries = true)
    public void deleteByGid(String goodsId) {
        QueryWrapper<LitemallRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        remove(wrapper);
    }

    @Cacheable(sync = true)
    public Integer countByGoodsId(String goodsId) {
        QueryWrapper<LitemallRewardTask> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        return Math.toIntExact(count(wrapper));
    }
    
    @Cacheable(sync = true)
    public List<LitemallRewardTask> querySelective(RewardListBody body) {
        QueryWrapper<LitemallRewardTask> wrapper = startPage(body);
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(LitemallRewardTask.GOODS_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }

}
