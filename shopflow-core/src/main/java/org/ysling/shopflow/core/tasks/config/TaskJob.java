package org.ysling.shopflow.core.tasks.config;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.enums.SystemOrderConfig;
import org.ysling.shopflow.core.tasks.handler.TaskHandler;
import org.ysling.shopflow.core.tasks.impl.*;
import org.ysling.shopflow.core.tasks.service.TaskService;
import org.ysling.shopflow.core.tenant.handler.TenantContextHolder;
import org.ysling.shopflow.db.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ysling.shopflow.db.service.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时检查所有执行失败的任务
 * @author Ysling
 */
@Slf4j
@Component
public class TaskJob {


    @Autowired
    private TaskJobService taskJobService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ITenantService tenantService;


    /**
     * 获取租户ID列表
     */
    private ArrayList<String> getTenantIds(){
        List<ShopflowTenant> tenantList = tenantService.list();
        ArrayList<String> idList = new ArrayList<>();
        for (ShopflowTenant tenant :tenantList) {
            idList.add(tenant.getId());
        }
        idList.add(TenantContextHolder.getDefaultId());
        return idList;
    }



    /**
     * 自动确认收货延时队列检查
     * <p>
     * 定时时间是每天凌晨1点。
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkOrderUnconfirmed() {
        TaskHandler taskHandler = new TaskHandler("订单自动确认收货", getTenantIds()) {
            @Override
            public Integer runTask() {
                List<ShopflowOrder> orderList = taskJobService.queryUnconfirmed();
                for (ShopflowOrder order : orderList) {
                    LocalDateTime ship = order.getShipTime();
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime expire = ship.plusDays(SystemOrderConfig.getOrderUnconfirmed());
                    if (expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new OrderUnconfirmedTask(order, 0));
                    } else {
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        taskService.addTask(new OrderUnconfirmedTask(order, delay));
                    }
                }
                return orderList.size();
            }
        };
        taskHandler.run();
    }

    /**
     * 团购延时队列检查
     * <p>
     * 定时时间是每天凌晨2点。
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkGrouponRuleExpired() {
        TaskHandler taskHandler = new TaskHandler("团购规则过期", getTenantIds()) {
            @Override
            public Integer runTask() {
                List<ShopflowGrouponRules> grouponRulesList = taskJobService.queryGrouponRulesExpired();
                for(ShopflowGrouponRules grouponRules : grouponRulesList){
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime expire =  grouponRules.getExpireTime();
                    if(expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new GrouponRuleExpiredTask(grouponRules, 0));
                    }
                    else{
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        taskService.addTask(new GrouponRuleExpiredTask(grouponRules, delay));
                    }
                }
                return grouponRulesList.size();
            }
        };
        taskHandler.run();
    }

    /**
     * 订单超时支付延时队列检查
     * <p>
     * 定时时间是每天凌晨3点。
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOrderUnpaid() {
        TaskHandler taskHandler = new TaskHandler("订单支付超时", getTenantIds()) {
            @Override
            public Integer runTask() {
                List<ShopflowOrder> orderList = taskJobService.queryUnpaid();
                for (ShopflowOrder order : orderList) {
                    LocalDateTime add = order.getAddTime();
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime expire = add.plusMinutes(SystemOrderConfig.getOrderUnpaid());
                    if (expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new OrderUnpaidTask(order, 0));
                    } else {
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        taskService.addTask(new OrderUnpaidTask(order, delay));
                    }
                }
                return orderList.size();
            }
        };
        taskHandler.run();
    }

    /**
     * 可评价订单商品超期
     * <p>
     * 定时时间是每天凌晨4点。
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void checkOrderComment() {
        TaskHandler taskHandler = new TaskHandler("订单评论超时", getTenantIds()) {
            @Override
            public Integer runTask() {
                List<ShopflowOrder> orderList = taskJobService.queryComment();
                for(ShopflowOrder order : orderList){
                    LocalDateTime add = order.getConfirmTime();
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime expire =  add.plusDays(SystemOrderConfig.getOrderComment());
                    if(expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new OrderCommentTask(order, 0));
                    } else{
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        taskService.addTask(new OrderCommentTask(order, delay));
                    }
                }
                return orderList.size();
            }
        };
        taskHandler.run();
    }

    /**
     * 检查优惠券是否已经过期
     * <p>
     * 定时时间是每天凌晨5点。
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void checkCouponExpired() {
        TaskHandler taskHandler = new TaskHandler("优惠券是否已经过期", getTenantIds()) {
            @Override
            public Integer runTask() {
                //查询所有已经过期优惠券
                List<ShopflowCoupon> couponList = taskJobService.queryCouponExpired();
                for(ShopflowCoupon coupon : couponList){
                    LocalDateTime expire = coupon.getEndTime();
                    LocalDateTime now = LocalDateTime.now();
                    if(expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new CouponExpiredTask(coupon, 0));
                    }
                    else{
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        log.info(String.format("系统定时任务 -> [查询-优惠券是否已经过期][超时时间=%s毫秒]" , delay));
                        taskService.addTask(new CouponExpiredTask(coupon, delay));
                    }
                }
                return couponList.size();
            }
        };
        taskHandler.run();
    }

    /**
     * 检查用户优惠券是否已经过期
     * <p>
     * 定时时间是每天凌晨6点。
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void checkCouponUserExpired() {
        TaskHandler taskHandler = new TaskHandler("用户优惠券是否已经过期", getTenantIds()) {
            @Override
            public Integer runTask() {
                //查询所有已经过期优惠券
                List<ShopflowCouponUser> couponList = taskJobService.queryCouponUserExpired();
                for(ShopflowCouponUser couponUser : couponList){
                    LocalDateTime expire = couponUser.getEndTime();
                    LocalDateTime now = LocalDateTime.now();
                    if(expire.isBefore(now)) {
                        // 已经过期，则加入延迟队列
                        taskService.addTask(new CouponUserExpiredTask(couponUser, 0));
                    }
                    else{
                        // 还没过期，则加入延迟队列
                        long delay = ChronoUnit.MILLIS.between(now, expire);
                        log.info(String.format("系统定时任务 -> [查询-用户优惠券是否已经过期][超时时间=%s毫秒]" , delay));
                        taskService.addTask(new CouponUserExpiredTask(couponUser, delay));
                    }
                }
                return couponList.size();
            }
        };
        taskHandler.run();
    }
}
