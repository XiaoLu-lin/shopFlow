package org.ysling.shopflow.db.mapper;

import org.ysling.shopflow.db.domain.ShopflowNoticeAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.Collection;
import java.io.Serializable;

/**
 * <p>
 * 通知管理员表 Mapper 接口
 * </p>
 *
 * @author ysling
 */
public interface NoticeAdminMapper extends BaseMapper<ShopflowNoticeAdmin> {

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
