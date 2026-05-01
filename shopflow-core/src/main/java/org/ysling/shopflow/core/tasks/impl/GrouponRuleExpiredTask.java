package org.ysling.shopflow.core.tasks.impl;
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

import lombok.extern.slf4j.Slf4j;
import org.ysling.shopflow.core.service.CommonService;
import org.ysling.shopflow.core.service.OrderCoreService;
import org.ysling.shopflow.core.tasks.service.TaskRunnable;
import org.ysling.shopflow.core.utils.BeanUtil;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowGroupon;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.enums.GrouponStatus;
import org.ysling.shopflow.db.enums.GrouponRuleStatus;
import org.ysling.shopflow.db.enums.OrderStatus;
import org.ysling.shopflow.db.service.IGoodsService;
import org.ysling.shopflow.db.service.IGrouponRulesService;
import org.ysling.shopflow.db.service.IGrouponService;
import org.ysling.shopflow.db.service.IOrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团购到期延时队列
 */
@Slf4j
public class GrouponRuleExpiredTask extends TaskRunnable {

    /**团购规则ID*/
    private final String grouponRuleId;
    /**id前缀*/
    private static final String idPrefix = "GrouponRuleExpiredTask-";
    /**任务名称*/
    private static final String taskName = "团购规则过期";


    public GrouponRuleExpiredTask(ShopflowGrouponRules grouponRules, long delayInMilliseconds){
        super(idPrefix + grouponRules.getId(), delayInMilliseconds, grouponRules.getTenantId(), taskName);
        this.grouponRuleId = grouponRules.getId();
    }

    public GrouponRuleExpiredTask(ShopflowGrouponRules grouponRules){
        super(idPrefix + grouponRules.getId(), 0, grouponRules.getTenantId(), taskName);
        this.grouponRuleId = grouponRules.getId();
    }

    @Override
    public void runTask() {
        CommonService commonService = BeanUtil.getBean(CommonService.class);
        OrderCoreService orderCoreService = BeanUtil.getBean(OrderCoreService.class);
        IOrderService orderService = BeanUtil.getBean(IOrderService.class);
        IGoodsService goodsService = BeanUtil.getBean(IGoodsService.class);
        IGrouponService grouponService = BeanUtil.getBean(IGrouponService.class);
        IGrouponRulesService grouponRulesService = BeanUtil.getBean(IGrouponRulesService.class);

        //查找团购规则
        ShopflowGrouponRules grouponRules = grouponRulesService.findById(grouponRuleId);
        if (grouponRules == null) {
            return;
        }
        if (!GrouponRuleStatus.RULE_STATUS_ON.equals(grouponRules.getStatus())) {
            return;
        }
        //查找团购商品
        ShopflowGoods goods = goodsService.findById(grouponRules.getGoodsId());
        if (goods == null) {
            return;
        }
        //设置商品取消团购
        goods.setIsGroupon(false);
        if (goodsService.updateVersionSelective(goods) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //判断是否到达过期时间
        if (grouponRules.getExpireTime().isBefore(LocalDateTime.now())){
            grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_DOWN_EXPIRE.getStatus());
        } else {
            grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_DOWN_ADMIN.getStatus());
        }
        // 团购活动取消
        if (grouponRulesService.updateVersionSelective(grouponRules) == 0) {
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        // 用户团购订单处理
        List<ShopflowGroupon> grouponList = commonService.queryByRuleId(grouponRuleId);
        for(ShopflowGroupon groupon : grouponList){
            ShopflowOrder order = orderService.findById(groupon.getOrderId());
            if(groupon.getStatus().equals(GrouponStatus.STATUS_NONE.getStatus())){
                //团购未付款或订单金额为零，设置团购取消
                groupon.setStatus(GrouponStatus.STATUS_CANCEL.getStatus());
                if (grouponService.updateVersionSelective(groupon) == 0) {
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
                //设置订单取消（系统）
                order.setOrderStatus(OrderStatus.STATUS_AUTO_CANCEL.getStatus());
                if (orderService.updateVersionSelective(order) == 0) {
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
            } else if(groupon.getStatus().equals(GrouponStatus.STATUS_ON.getStatus())){
                // 如果团购进行中，团购设置团购失败等待退款状态
                groupon.setStatus(GrouponStatus.STATUS_FAIL.getStatus());
                if (grouponService.updateVersionSelective(groupon) == 0) {
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
                //支付金额为零
                if (order.getActualPrice().compareTo(BigDecimal.ZERO) <= 0){
                    //团购订单申请退款
                    order.setOrderStatus(OrderStatus.STATUS_AUTO_CANCEL.getStatus());
                    // 返还订单
                    orderCoreService.orderRelease(order);
                } else {
                    //团购订单申请退款
                    order.setOrderStatus(OrderStatus.STATUS_REFUND.getStatus());
                }
                //更新订单
                if (orderService.updateVersionSelective(order) == 0) {
                    throw new RuntimeException("网络繁忙，请刷新重试");
                }
            }
        }
    }

}
