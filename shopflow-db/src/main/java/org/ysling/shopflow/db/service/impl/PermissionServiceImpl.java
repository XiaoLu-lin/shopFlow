package org.ysling.shopflow.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.ysling.shopflow.db.domain.ShopflowPermission;
import org.ysling.shopflow.db.mapper.PermissionMapper;
import org.ysling.shopflow.db.service.IPermissionService;
import org.ysling.shopflow.db.mybatis.IBaseServiceImpl;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author ysling
 */
@Service
@Primary
@CacheConfig(cacheNames = "shopflow_permission")
public class PermissionServiceImpl extends IBaseServiceImpl<PermissionMapper, ShopflowPermission> implements IPermissionService {

    @Override
    @Cacheable(sync = true)
    public ShopflowPermission findById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowPermission> queryAll(Wrapper<ShopflowPermission> queryWrapper) {
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int add(ShopflowPermission record) {
        return getBaseMapper().insert(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean batchAdd(List<ShopflowPermission> list) {
        return saveBatch(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateSelective(ShopflowPermission record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateVersionSelective(ShopflowPermission record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Serializable id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean exists(Wrapper<ShopflowPermission> queryWrapper) {
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
    public boolean save(ShopflowPermission entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveBatch(Collection<ShopflowPermission> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowPermission entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowPermission entity, Wrapper<ShopflowPermission> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdateBatch(Collection<ShopflowPermission> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(ShopflowPermission entity) {
        return super.removeById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(Wrapper<ShopflowPermission> queryWrapper) {
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
    public boolean updateById(ShopflowPermission entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Wrapper<ShopflowPermission> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(ShopflowPermission entity, Wrapper<ShopflowPermission> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatchById(Collection<ShopflowPermission> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowPermission getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowPermission> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowPermission> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowPermission getOne(Wrapper<ShopflowPermission> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public long count() {
        return super.count();
    }

    @Override
    @Cacheable(sync = true)
    public long count(Wrapper<ShopflowPermission> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowPermission> list(Wrapper<ShopflowPermission> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowPermission> list() {
        return super.list();
    }

    @Override
    @Cacheable(sync = true)
    public List<Map<String, Object>> listMaps(Wrapper<ShopflowPermission> queryWrapper) {
        return super.listMaps(queryWrapper);
    }


}
