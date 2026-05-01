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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动时初始化
 * @author Ysling
 */
@Slf4j
@Component
public class TaskStartupRunner implements ApplicationRunner {

    @Autowired
    private TaskJob taskJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化 -> [初始化定时任务]");
        //订单超时未支付
        taskJob.checkOrderUnpaid();
        //订单评论超时
        taskJob.checkOrderComment();
        //订单确认收货超时
        taskJob.checkOrderUnconfirmed();
        //订单团购超时
        taskJob.checkGrouponRuleExpired();
        //优惠券过期
        taskJob.checkCouponExpired();
        //用户优惠券过期
        taskJob.checkCouponUserExpired();
    }
}