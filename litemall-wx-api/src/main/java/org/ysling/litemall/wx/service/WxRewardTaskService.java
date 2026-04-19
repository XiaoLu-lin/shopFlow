package org.ysling.litemall.wx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.db.domain.LitemallRewardTask;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.RewardTaskStatus;
import org.ysling.litemall.db.service.impl.RewardTaskServiceImpl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;


/**
 * 赏金规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_reward_task")
public class WxRewardTaskService extends RewardTaskServiceImpl {


    /**
     * 获取两数百分比
     * @param divisor 除数
     * @param dividend 被除数
     * @return 百分比
     */
    public String percentage(Integer divisor, Integer dividend){
        BigDecimal divideNum = BigDecimal.valueOf(divisor).divide(
                BigDecimal.valueOf(dividend), 2, RoundingMode.CEILING);
        //将结果百分比
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        return percent.format(divideNum.doubleValue());
    }


    @Cacheable(sync = true)
    public List<LitemallRewardTask> querySelective(String goodsId, PageBody body) {
        QueryWrapper<LitemallRewardTask> wrapper = startPage(body);
        if (StringUtils.hasText(goodsId)) {
            wrapper.eq(LitemallRewardTask.GOODS_ID , goodsId);
        }
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallRewardTask> queryByReward(Integer limit) {
        QueryWrapper<LitemallRewardTask> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallRewardTask.STATUS , RewardTaskStatus.TASK_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallRewardTask> querySelective(PageBody body) {
        QueryWrapper<LitemallRewardTask> wrapper = startPage(body);
        wrapper.eq(LitemallRewardTask.STATUS , RewardTaskStatus.TASK_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

}
