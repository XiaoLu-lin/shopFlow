package org.ysling.shopflow.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.ysling.shopflow.db.domain.ShopflowGoodsProduct;
import org.ysling.shopflow.db.mapper.GoodsProductMapper;
import org.ysling.shopflow.db.service.IGoodsProductService;
import org.ysling.shopflow.db.mybatis.IBaseServiceImpl;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 商品货品表 服务实现类
 * </p>
 *
 * @author ysling
 */
@Service
@Primary
@CacheConfig(cacheNames = "shopflow_goods_product")
public class GoodsProductServiceImpl extends IBaseServiceImpl<GoodsProductMapper, ShopflowGoodsProduct> implements IGoodsProductService {

    @Override
    @Cacheable(sync = true)
    public ShopflowGoodsProduct findById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowGoodsProduct> queryAll(Wrapper<ShopflowGoodsProduct> queryWrapper) {
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int add(ShopflowGoodsProduct record) {
        return getBaseMapper().insert(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean batchAdd(List<ShopflowGoodsProduct> list) {
        return saveBatch(list);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateSelective(ShopflowGoodsProduct record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int updateVersionSelective(ShopflowGoodsProduct record) {
        return getBaseMapper().updateById(record);
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Serializable id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean exists(Wrapper<ShopflowGoodsProduct> queryWrapper) {
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
    public boolean save(ShopflowGoodsProduct entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveBatch(Collection<ShopflowGoodsProduct> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowGoodsProduct entity) {
        return super.saveOrUpdate(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdate(ShopflowGoodsProduct entity, Wrapper<ShopflowGoodsProduct> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean saveOrUpdateBatch(Collection<ShopflowGoodsProduct> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeById(ShopflowGoodsProduct entity) {
        return super.removeById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(Wrapper<ShopflowGoodsProduct> queryWrapper) {
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
    public boolean updateById(ShopflowGoodsProduct entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Wrapper<ShopflowGoodsProduct> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(ShopflowGoodsProduct entity, Wrapper<ShopflowGoodsProduct> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatchById(Collection<ShopflowGoodsProduct> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowGoodsProduct getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowGoodsProduct> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowGoodsProduct> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    @Cacheable(sync = true)
    public ShopflowGoodsProduct getOne(Wrapper<ShopflowGoodsProduct> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public long count() {
        return super.count();
    }

    @Override
    @Cacheable(sync = true)
    public long count(Wrapper<ShopflowGoodsProduct> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowGoodsProduct> list(Wrapper<ShopflowGoodsProduct> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    @Cacheable(sync = true)
    public List<ShopflowGoodsProduct> list() {
        return super.list();
    }

    @Override
    @Cacheable(sync = true)
    public List<Map<String, Object>> listMaps(Wrapper<ShopflowGoodsProduct> queryWrapper) {
        return super.listMaps(queryWrapper);
    }


}
