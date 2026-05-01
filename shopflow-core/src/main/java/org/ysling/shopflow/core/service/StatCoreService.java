package org.ysling.shopflow.core.service;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.service.IOrderGoodsService;
import org.ysling.shopflow.db.service.IOrderService;
import org.ysling.shopflow.db.service.IUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计服务
 * @author Ysling
 */
@Service
public class StatCoreService {


    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderGoodsService orderGoodsService;

    /**
     * 统计用户数据
     */
    public List<Map<String, Object>> statUser() {
        QueryWrapper<ShopflowUser> wrapper = new QueryWrapper<>();
        wrapper.select("SUBSTR(add_time, 1, 10) AS day", "COUNT(DISTINCT id) AS users");
        wrapper.groupBy("SUBSTR(add_time, 1, 10)");
        return userService.listMaps(wrapper);
    }

    /**
     * 统计订单数据
     */
    public List<Map<String, Object>> statOrder() {
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.select("SUBSTR(add_time, 1, 10) AS day"
                , "COUNT(DISTINCT id) AS orders"
                , "COUNT(DISTINCT user_id) AS customers"
                , "SUM(actual_price) AS amount"
                , "ROUND(SUM(actual_price)/COUNT(DISTINCT user_id),2) AS pcr"
        );
        wrapper.groupBy("SUBSTR(add_time, 1, 10)");
        ArrayList<Short> status = new ArrayList<>();
        status.add(OrderStatus.STATUS_SHIP.getStatus());
        status.add(OrderStatus.STATUS_CONFIRM.getStatus());
        wrapper.in(ShopflowOrder.ORDER_STATUS , status);
        return orderService.listMaps(wrapper);
    }

    /**
     * 统计订单商品数据
     */
    public List<Map<String, Object>> statGoods() {
        QueryWrapper<ShopflowOrderGoods> wrapper = new QueryWrapper<>();
        wrapper.select("SUBSTR(add_time, 1, 10) AS day"
                , "COUNT(DISTINCT order_id) AS orders"
                , "SUM(number) AS products"
                , "SUM(number*price) AS amount"
        );
        wrapper.groupBy("SUBSTR(add_time, 1, 10)");
        return orderGoodsService.listMaps(wrapper);
    }


}
