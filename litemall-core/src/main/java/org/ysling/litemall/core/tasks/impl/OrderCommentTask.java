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
import org.ysling.litemall.core.service.CommonService;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.system.enums.SystemOrderConfig;
import org.ysling.litemall.core.tasks.service.TaskRunnable;
import org.ysling.litemall.core.utils.BeanUtil;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.enums.OrderStatus;
import org.ysling.litemall.db.service.IOrderGoodsService;
import org.ysling.litemall.db.service.IOrderService;
import java.util.List;

/**
 * 订单评论超时
 * @author Ysling
 */
@Slf4j
public class OrderCommentTask extends TaskRunnable {

    /**订单ID*/
    private final String orderId;
    /**id前缀*/
    private static final String idPrefix = "OrderCommentTask-";
    /**任务名称*/
    private static final String taskName = "订单评论超时";
    /**默认延时时间 单位毫秒*/
    private static final long defaultSeconds = SystemOrderConfig.getOrderComment() * 24 * 60 * 60 * 1000;


    public OrderCommentTask(LitemallOrder order, long delayInMilliseconds){
        super(idPrefix + order.getId(), delayInMilliseconds, order.getTenantId() , taskName);
        this.orderId = order.getId();
    }

    public OrderCommentTask(LitemallOrder order){
        super(idPrefix + order.getId(), defaultSeconds, order.getTenantId() , taskName);
        this.orderId = order.getId();
    }

    @Override
    public void runTask() {
        CommonService commonService = BeanUtil.getBean(CommonService.class);
        IOrderService orderService = BeanUtil.getBean(IOrderService.class);
        IOrderGoodsService orderGoodsService = BeanUtil.getBean(IOrderGoodsService.class);

        LitemallOrder order = orderService.findById(this.orderId);
        if(order == null || !OrderStatus.isConfirmStatus(order)) {
            return;
        }

        order.setComments((short) 0);
        order.setOrderStatus(OrderStatus.STATUS_COMMENT_OVERTIME.getStatus());
        if (orderService.updateVersionSelective(order) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //设置订单商品禁止评论
        List<LitemallOrderGoods> orderGoodsList = commonService.queryByOid(order.getId());
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            orderGoods.setComment(-1);
            if (orderGoodsService.updateVersionSelective(orderGoods) == 0){
                throw new RuntimeException("商品评论更新失败");
            }
        }
    }



}
