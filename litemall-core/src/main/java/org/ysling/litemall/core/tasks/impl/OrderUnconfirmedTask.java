package org.ysling.litemall.core.tasks.impl;
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
import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.service.DealingSlipCoreService;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.system.enums.SystemOrderConfig;
import org.ysling.litemall.core.tasks.service.TaskRunnable;
import org.ysling.litemall.core.utils.BeanUtil;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.enums.OrderStatus;
import org.ysling.litemall.db.service.IBrandService;
import org.ysling.litemall.db.service.IOrderService;
import java.time.LocalDateTime;

/**
 * 订单自动确认收货延时队列
 * @author Ysling
 */
@Slf4j
public class OrderUnconfirmedTask extends TaskRunnable {

    /**订单ID*/
    private final String orderId;
    /**id前缀*/
    private static final String idPrefix = "OrderUnconfirmedTask-";
    /**任务名称*/
    private static final String taskName = "订单自动收货";
    /**默认延时时间 单位毫秒*/
    private static final long defaultSeconds = SystemOrderConfig.getOrderUnconfirmed() * 24 * 60 * 60 * 1000;


    public OrderUnconfirmedTask(LitemallOrder order, long delayInMilliseconds){
        super(idPrefix + order.getId(), delayInMilliseconds, order.getTenantId(), taskName);
        this.orderId = order.getId();
    }

    public OrderUnconfirmedTask(LitemallOrder order){
        super(idPrefix + order.getId(), defaultSeconds, order.getTenantId(), taskName);
        this.orderId = order.getId();
    }

    @Override
    public void runTask() {
        IOrderService orderService = BeanUtil.getBean(IOrderService.class);
        IBrandService brandService = BeanUtil.getBean(IBrandService.class);
        DealingSlipCoreService slipCoreService = BeanUtil.getBean(DealingSlipCoreService.class);

        LitemallOrder order = orderService.findById(this.orderId);
        //判断是否满足收货条件
        if(order == null || !OrderStatus.isShipStatus(order)) {
            return;
        }
        //获取店铺信息
        LitemallBrand brand = brandService.findById(order.getBrandId());
        if (brand == null) {
            throw new RuntimeException("店铺信息获取失败");
        }

        // 设置订单已收货状态
        order.setConfirmTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.STATUS_AUTO_CONFIRM.getStatus());
        if (orderService.updateVersionSelective(order) == 0) {
            throw new RuntimeException("订单 ID=" + order.getId() +"数据更新失败");
        }

        //获取店铺所有者向店家发放余额
        slipCoreService.addOrderIntegral(order , brand);
    }
}
