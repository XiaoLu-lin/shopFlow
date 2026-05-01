package org.ysling.shopflow.db.mapper;

import org.ysling.shopflow.db.domain.ShopflowGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.Collection;
import java.io.Serializable;

/**
 * <p>
 * 商品基本信息表 Mapper 接口
 * </p>
 *
 * @author ysling
 */
public interface GoodsMapper extends BaseMapper<ShopflowGoods> {

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
