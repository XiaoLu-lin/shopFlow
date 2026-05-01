package org.ysling.shopflow.admin.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;
import org.ysling.shopflow.db.service.impl.OrderGoodsServiceImpl;
import java.util.List;

/**
 * 订单商品表
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_order_goods")
public class AdminOrderGoodsService extends OrderGoodsServiceImpl {

    @Cacheable(sync = true)
    public List<ShopflowOrderGoods> queryByOid(String orderId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public ShopflowOrderGoods findByOrderId(String orderId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return getOne(wrapper , false);
    }

    @CacheEvict(allEntries = true)
    public void deleteByOrderId(String orderId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        remove(wrapper);
    }

}
