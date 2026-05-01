package org.ysling.shopflow.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.ysling.shopflow.db.domain.ShopflowCoupon;
import org.ysling.shopflow.db.mapper.CouponMapper;
import org.ysling.shopflow.db.service.ICouponService;
import org.ysling.shopflow.db.mybatis.IBaseServiceImpl;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 优惠券信息及规则表 服务实现类
 * </p>
 *
 * @author ysling
 */
@Service
@Primary
@CacheConfig(cacheNames = "shopflow_coupon")
public class CouponServiceImpl extends IBaseServiceImpl<CouponMapper, ShopflowCoupon> implements ICouponService {

    @Override
    @Cacheable(sync = true)
    public ShopflowCoupon findById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowCoupon> queryAll(Wrapper<ShopflowCoupon> queryWrapper) {
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int add(ShopflowCoupon record) {
        return getBaseMapper().insert(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean batchAdd(List<ShopflowCoupon> list) {
        return saveBatch(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateSelective(ShopflowCoupon record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateVersionSelective(ShopflowCoupon record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Serializable id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean exists(Wrapper<ShopflowCoupon> queryWrapper) {
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
    public boolean save(ShopflowCoupon entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveBatch(Collection<ShopflowCoupon> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowCoupon entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowCoupon entity, Wrapper<ShopflowCoupon> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdateBatch(Collection<ShopflowCoupon> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(ShopflowCoupon entity) {
        return super.removeById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(Wrapper<ShopflowCoupon> queryWrapper) {
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
    public boolean updateById(ShopflowCoupon entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Wrapper<ShopflowCoupon> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(ShopflowCoupon entity, Wrapper<ShopflowCoupon> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatchById(Collection<ShopflowCoupon> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowCoupon getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowCoupon> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowCoupon> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowCoupon getOne(Wrapper<ShopflowCoupon> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public long count() {
        return super.count();
    }

    @Override
    @Cacheable(sync = true)
    public long count(Wrapper<ShopflowCoupon> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowCoupon> list(Wrapper<ShopflowCoupon> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowCoupon> list() {
        return super.list();
    }

    @Override
    @Cacheable(sync = true)
    public List<Map<String, Object>> listMaps(Wrapper<ShopflowCoupon> queryWrapper) {
        return super.listMaps(queryWrapper);
    }


}
