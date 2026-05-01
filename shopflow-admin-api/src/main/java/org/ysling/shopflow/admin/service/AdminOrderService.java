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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.order.body.OrderListBody;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.service.impl.OrderServiceImpl;
import java.util.List;

/**
 * 订单服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_order")
public class AdminOrderService extends OrderServiceImpl {

    
    @Cacheable(sync = true)
    public List<ShopflowOrder> querySelective(OrderListBody body) {
        QueryWrapper<ShopflowOrder> wrapper = startPage(body);
        if (body.getBrandId() != null){
            wrapper.eq(ShopflowOrder.BRAND_ID , body.getBrandId());
        }
        if (body.getGoodsId() != null){
            wrapper.eq(ShopflowOrder.GOODS_ID , body.getGoodsId());
        }
        if(body.getEnd() != null){
            wrapper.le(ShopflowOrder.ADD_TIME , body.getEnd());
        }
        if(body.getStart() != null){
            wrapper.ge(ShopflowOrder.ADD_TIME , body.getStart());
        }
        if (StringUtils.hasText(body.getShipSn())) {
            wrapper.le(ShopflowOrder.SHIP_SN , body.getShipSn());
        }
        if (StringUtils.hasText(body.getMobile())) {
            wrapper.like(ShopflowOrder.MOBILE , body.getMobile());
        }
        if (StringUtils.hasText(body.getOrderSn())) {
            wrapper.like(ShopflowOrder.ORDER_SN , body.getOrderSn());
        }
        if (body.getOrderStatusArray() != null && body.getOrderStatusArray().size() > 0) {
            wrapper.in(ShopflowOrder.ORDER_STATUS , body.getOrderStatusArray());
        }
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public Integer statusCount(List<Short> orderStatus){
        if (orderStatus == null || orderStatus.size() <= 0){
            return 0;
        }
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.in(ShopflowOrder.ORDER_STATUS , orderStatus);
        return Math.toIntExact(count(wrapper));
    }

}
