package org.ysling.litemall.db.mapper;

import org.ysling.litemall.db.domain.LitemallReward;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.Collection;
import java.io.Serializable;

/**
 * <p>
 * 赏金任务参与表 Mapper 接口
 * </p>
 *
 * @author ysling
 */
public interface RewardMapper extends BaseMapper<LitemallReward> {

    /**
     * 真实删除
     * @param id 主键
     * @return 影响行数
     */
    int actualDeleteById(Serializable id);

    /**
     * 批量真实删除
     * @param idList 主键列表
     * @return  影响行数
     */
    int actualDeleteByIds(Collection<? extends Serializable> idList);

}
