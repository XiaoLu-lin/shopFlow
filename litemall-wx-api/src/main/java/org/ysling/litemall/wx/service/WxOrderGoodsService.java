package org.ysling.litemall.wx.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
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
import org.ysling.litemall.db.domain.LitemallOrderGoods;
import org.ysling.litemall.db.service.impl.OrderGoodsServiceImpl;

/**
 * 订单商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_order_goods")
public class WxOrderGoodsService extends OrderGoodsServiceImpl {


    @Cacheable(sync = true)
    public Integer getComments(String orderId) {
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public boolean checkExist(String goodsId) {
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.GOODS_ID , goodsId);
        return exists(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallOrderGoods findByOrderId(String orderId) {
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        return getOne(wrapper , false);
    }

    @CacheEvict(allEntries = true)
    public void deleteByOrderId(String orderId) {
        QueryWrapper<LitemallOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrderGoods.ORDER_ID , orderId);
        remove(wrapper);
    }


}
