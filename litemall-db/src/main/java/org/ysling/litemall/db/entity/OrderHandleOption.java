package org.ysling.litemall.db.entity;
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
import lombok.Data;

import java.io.Serializable;

/**
 * 用户可执行操作类
 * @author Ysling
 */
@Data
public class OrderHandleOption implements Serializable {

    /**取消操作*/
    private boolean cancel = false;
    /**删除操作*/
    private boolean delete = false;
    /**支付操作*/
    private boolean pay = false;
    /**评论操作*/
    private boolean comment = false;
    /**确认收货操作*/
    private boolean confirm = false;
    /**取消订单并退款操作*/
    private boolean refund = false;
    /**再次购买*/
    private boolean rebuy = false;
    /**售后操作*/
    private boolean aftersale = false;

}
