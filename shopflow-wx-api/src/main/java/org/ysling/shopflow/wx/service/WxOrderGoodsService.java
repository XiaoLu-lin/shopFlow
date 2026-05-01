package org.ysling.shopflow.wx.service;
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

/**
 * 订单商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_order_goods")
public class WxOrderGoodsService extends OrderGoodsServiceImpl {


    @Cacheable(sync = true)
    public Integer getComments(String orderId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.ORDER_ID , orderId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public boolean checkExist(String goodsId) {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrderGoods.GOODS_ID , goodsId);
        return exists(wrapper);
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
