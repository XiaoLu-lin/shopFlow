package org.ysling.shopflow.db.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageHelper;
import org.ysling.shopflow.db.domain.ShopflowFeedback;
import org.ysling.shopflow.db.entity.PageBody;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 自定义通用接口
 * @author Ysling
 * @param <T>   实体类
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * 添加
     * @param record 实体类
     * @return 影响行数
     */
    int add(T record);

    /**
     * 根据id查询数据
     * @param id 主键
     * @return 实体类
     */
    T findById(Serializable id);

    /**
     * 根据条件查询列表
     * @param queryWrapper 条件
     * @return 实体类列表
     */
    List<T> queryAll(Wrapper<T> queryWrapper);

    /**
     * 根据条件查询是否存在
     * @param queryWrapper 条件
     * @return true,false
     */
    boolean exists(Wrapper<T> queryWrapper);

    /**
     * 批量插入数据
     * @param list 实体列表
     * @return 影响行数
     */
    boolean batchAdd(List<T> list);

    /**
     * 更新
     * @param record 实体类
     * @return 影响行数
     */
    int updateSelective(T record);

    /**
     * 使用乐观锁更新
     * @param record 实体类
     * @return 影响行数
     */
    int updateVersionSelective(T record);

    /**
     * 根据id删除
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Serializable id);

    /**
     * 真实删除
     * @param id 主键
     * @return 影响行数
     */
    default int actualDeleteById(Serializable id){
        return 0;
    }

    /**
     * 批量真实删除
     * @param idList 主键列表
     * @return  影响行数
     */
    default int actualDeleteByIds(Collection<? extends Serializable> idList){
        return 0;
    }


    /**
     * 开启分页排序
     * @param body 分页实体
     * @return 排序拼接字符串
     */
    default QueryWrapper<T> startPage(PageBody body){
        if (body.getPage() != null && body.getLimit() != null && body.getLimit() > 0){
            PageHelper.clearPage();
            PageHelper.startPage(body.getPage(), body.getLimit());
        }
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (body.getId() != null){
            wrapper.eq(PageBody.ID, body.getId());
        }
        wrapper.orderBy(true, "asc".equals(body.getOrder()), body.getSort());
        return wrapper;
    }

}
