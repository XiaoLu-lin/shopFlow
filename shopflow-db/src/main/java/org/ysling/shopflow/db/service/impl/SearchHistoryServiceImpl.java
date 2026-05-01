package org.ysling.shopflow.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.ysling.shopflow.db.domain.ShopflowSearchHistory;
import org.ysling.shopflow.db.mapper.SearchHistoryMapper;
import org.ysling.shopflow.db.service.ISearchHistoryService;
import org.ysling.shopflow.db.mybatis.IBaseServiceImpl;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 搜索历史表 服务实现类
 * </p>
 *
 * @author ysling
 */
@Service
@Primary
@CacheConfig(cacheNames = "shopflow_search_history")
public class SearchHistoryServiceImpl extends IBaseServiceImpl<SearchHistoryMapper, ShopflowSearchHistory> implements ISearchHistoryService {

    @Override
    @Cacheable(sync = true)
    public ShopflowSearchHistory findById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> queryAll(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int add(ShopflowSearchHistory record) {
        return getBaseMapper().insert(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean batchAdd(List<ShopflowSearchHistory> list) {
        return saveBatch(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateSelective(ShopflowSearchHistory record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateVersionSelective(ShopflowSearchHistory record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Serializable id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean exists(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return getBaseMapper().exists(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int actualDeleteById(Serializable id) {
        return getBaseMapper().actualDeleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int actualDeleteByIds(Collection<? extends Serializable> idList) {
        return getBaseMapper().actualDeleteByIds(idList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean save(ShopflowSearchHistory entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveBatch(Collection<ShopflowSearchHistory> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowSearchHistory entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowSearchHistory entity, Wrapper<ShopflowSearchHistory> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdateBatch(Collection<ShopflowSearchHistory> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(ShopflowSearchHistory entity) {
        return super.removeById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<?> list) {
        return super.removeByIds(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        return super.removeByIds(list, useFill);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeBatchByIds(Collection<?> list) {
        return super.removeBatchByIds(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeBatchByIds(Collection<?> list, boolean useFill) {
        return super.removeBatchByIds(list, useFill);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(ShopflowSearchHistory entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Wrapper<ShopflowSearchHistory> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(ShopflowSearchHistory entity, Wrapper<ShopflowSearchHistory> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatchById(Collection<ShopflowSearchHistory> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowSearchHistory getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowSearchHistory getOne(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public long count() {
        return super.count();
    }

    @Override
    @Cacheable(sync = true)
    public long count(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> list(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> list() {
        return super.list();
    }

    @Override
    @Cacheable(sync = true)
    public List<Map<String, Object>> listMaps(Wrapper<ShopflowSearchHistory> queryWrapper) {
        return super.listMaps(queryWrapper);
    }


}
