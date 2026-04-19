package org.ysling.litemall.db.enums;
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
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.entity.OrderHandleOption;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，用户申请退款；203，管理员执行退款操作，确认退款成功；
 * 301 商家发货，用户未确认；
 * 302 用户确认收货，订单结束； 303 用户没有确认收货，但是快递反馈已收货后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消或者付款
 * 当201支付完成而商家未发货时，此时用户可以退款
 * 当301商家已发货时，此时用户可以有确认收货
 * 当302用户确认收货以后，此时用户可以进行的操作是退货、删除、去评价或者再次购买
 * 当303系统自动确认收货以后，此时用户可以删除、去评价、或者再次购买
 *
 */
public enum OrderStatus implements Serializable {
    
    /**未付款*/
    STATUS_CREATE ((short) 101 , "待付款"),
    /**已取消(用户)*/
    STATUS_USER_CANCEL ((short) 102 , "已取消(用户)"),
    /**已取消(系统)*/
    STATUS_AUTO_CANCEL ((short) 103 , "已取消(系统)"),
    /**已取消(管理员)*/
    STATUS_ADMIN_CANCEL ((short) 104 , "已取消(管理员)"),
    /**已取消(商家)*/
    STATUS_BRAND_CANCEL ((short) 105 , "已取消(商家)"),

    /**已付款(无需退款，订单金额为零)*/
    STATUS_BTL_PAY ((short) 200 , "已付款"),
    /**已付款*/
    STATUS_PAY ((short) 201 , "已付款"),
    /**已取消(退款中)*/
    STATUS_REFUND ((short) 202 , "已取消(退款中)"),
    /**已退款*/
    STATUS_REFUND_CONFIRM ((short) 203 , "已退款"),

    /**待开团（未支付）*/
    STATUS_GROUPON_NONE ((short) 301 , "待开团"),
    /**团购中（已支付）*/
    STATUS_GROUPON_ON ((short) 302 , "团购中"),
    /**团购失败（待退款）*/
    STATUS_GROUPON_FAIL ((short) 303 , "团购失败"),
    /**团购成功（待发货）*/
    STATUS_GROUPON_SUCCEED ((short) 304 , "团购成功"),

    /**已发货*/
    STATUS_SHIP ((short) 401 , "已发货"),
    /**已收货*/
    STATUS_CONFIRM ((short) 402 , "已收货"),
    /**已收货(系统)*/
    STATUS_AUTO_CONFIRM ((short) 403 , "已收货(系统)"),
    /**评价已超时*/
    STATUS_COMMENT_OVERTIME ((short) 404 , "评价已超时"),
    /**交易完成*/
    ORDER_SUCCEED ((short) 405 , "交易完成"),

    /**售后申请中*/
    STATUS_PUT_AFTERSALE ((short) 601 , "售后申请中"),
    /**售后退款中*/
    STATUS_DISPOSE_AFTERSALE ((short) 602 , "售后退款中"),
    /**售后已退款*/
    STATUS_FINISH_AFTERSALE ((short) 603 , "售后已退款"),
    /**售后已拒绝*/
    STATUS_REJECT_AFTERSALE ((short) 604 , "售后已拒绝");


    /**状态*/
    private final Short status;
    /**描述*/
    private final String depict;

    /**状态*/
    public Short getStatus() {
        return status;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }


    OrderStatus(Short status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 状态判断
     */
    public Boolean equals(Short status){
        return this.getStatus().equals(status);
    }

    /**
     * 根据状态获取描述
     * @param order 订单
     * @return 返回描述
     */
    public static String orderStatusText(LitemallOrder order) {
        if (order != null) {
            Short status = order.getOrderStatus();
            for (OrderStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

    public static OrderHandleOption build(LitemallOrder order) {
        Short status = order.getOrderStatus();
        OrderHandleOption handleOption = new OrderHandleOption();
        if (status.equals(STATUS_CREATE.getStatus()) || status.equals(STATUS_GROUPON_NONE.getStatus())) {
            // 如果订单没有被取消，且没有支付，则可支付，可取消
            handleOption.setCancel(true);
            handleOption.setPay(true);
        } else if (status.equals(STATUS_BTL_PAY.getStatus())) {
            // 如果订单没有被取消，且没有支付，可取消
            handleOption.setCancel(true);
        } else if (status.equals(STATUS_USER_CANCEL.getStatus()) || status.equals(STATUS_AUTO_CANCEL.getStatus())) {
            // 如果订单已经取消或是已完成，则可删除
            handleOption.setDelete(true);
        } else if (status.equals(STATUS_ADMIN_CANCEL.getStatus()) || status.equals(STATUS_BRAND_CANCEL.getStatus())) {
            // 如果订单已经取消或是已完成，则可删除
            handleOption.setDelete(true);
        } else if (status.equals(STATUS_PAY.getStatus()) || status.equals(STATUS_GROUPON_ON.getStatus())) {
            // 如果订单已付款，没有发货，则可退款
            if (order.getActualPrice().compareTo(BigDecimal.ZERO) <= 0){
                handleOption.setCancel(true);
            }else {
                handleOption.setRefund(true);
            }
        } else if (status.equals(STATUS_REFUND_CONFIRM.getStatus())) {
            // 如果订单已经退款，则可删除
            handleOption.setDelete(true);
        } else if (status.equals(STATUS_SHIP.getStatus())) {
            // 如果订单已经发货，没有收货，则可收货操作,可申请售后
            handleOption.setConfirm(true);
            handleOption.setAftersale(true);
        } else if (status.equals(STATUS_CONFIRM.getStatus()) || status.equals(STATUS_AUTO_CONFIRM.getStatus())) {
            // 如果订单已经支付，且已经收货，则可删除、去评论、和再次购买
            handleOption.setDelete(true);
            handleOption.setComment(true);
            handleOption.setRebuy(true);
        }else if (status.equals(STATUS_COMMENT_OVERTIME.getStatus()) || status.equals(ORDER_SUCCEED.getStatus())) {
            // 评论超时禁止评论
            handleOption.setDelete(true);
            handleOption.setRebuy(true);
        }else if (status.equals(STATUS_PUT_AFTERSALE.getStatus())) {
            // 售后申请中
        }else if (status.equals(STATUS_DISPOSE_AFTERSALE.getStatus())) {
            // 售后处理中
        }else if (status.equals(STATUS_FINISH_AFTERSALE.getStatus())) {
            // 售后已完成
            handleOption.setDelete(true);
        }else if (status.equals(STATUS_REJECT_AFTERSALE.getStatus())) {
            // 售后已拒绝
            handleOption.setDelete(true);
            handleOption.setAftersale(true);
        }

        return handleOption;
    }

    public static Integer orderBasics(LitemallOrder order) {
        Short status = order.getOrderStatus();
        if(STATUS_CREATE.getStatus().equals(status)){
            return 0;
        }else if(STATUS_BTL_PAY.getStatus().equals(status) || STATUS_PAY.getStatus().equals(status)){
            return 1;
        }else if(STATUS_SHIP.getStatus().equals(status)){
            return 2;
        }else if(STATUS_CONFIRM.getStatus().equals(status) || STATUS_AUTO_CONFIRM.getStatus().equals(status)){
            return 3;
        }else{
            return -1;
        }
    }

    public static List<Short> orderStatus(Integer showType) {
        // 全部订单
        if (showType.equals(0)) {
            return null;
        }
        List<Short> status = new ArrayList<>();
        if (showType.equals(1)) {
            // 待付款订单
            status.add(STATUS_CREATE.getStatus());
            status.add(STATUS_GROUPON_NONE.getStatus());
        } else if (showType.equals(2)) {
            // 待发货订单
            status.add(STATUS_PAY.getStatus());
            status.add(STATUS_BTL_PAY.getStatus());
            status.add(STATUS_GROUPON_ON.getStatus());
            status.add(STATUS_GROUPON_SUCCEED.getStatus());
        } else if (showType.equals(3)) {
            // 待收货订单
            status.add(STATUS_SHIP.getStatus());
        } else if (showType.equals(4)) {
            // 待评价订单
            status.add(STATUS_CONFIRM.getStatus());
            status.add(STATUS_AUTO_CONFIRM.getStatus());
        } else {
            return null;
        }
        return status;
    }

    public static List<Short> orderAdminStatus(Integer showType) {
        // 全部订单
        if (showType.equals(0)) {
            return null;
        }
        List<Short> status = new ArrayList<>();
        if (showType.equals(1)) {
            // 待付款订单
            status.add(STATUS_CREATE.getStatus());
            status.add(STATUS_GROUPON_NONE.getStatus());
        } else if (showType.equals(2)) {
            // 待发货订单
            status.add(STATUS_PAY.getStatus());
            status.add(STATUS_BTL_PAY.getStatus());
            status.add(STATUS_GROUPON_ON.getStatus());
            status.add(STATUS_GROUPON_SUCCEED.getStatus());
        } else if (showType.equals(3)) {
            // 待收货订单
            status.add(STATUS_SHIP.getStatus());
        } else if (showType.equals(4)) {
            // 已完成订单
            status.add(STATUS_CONFIRM.getStatus());
            status.add(STATUS_AUTO_CONFIRM.getStatus());
            status.add(STATUS_COMMENT_OVERTIME.getStatus());
            status.add(STATUS_COMMENT_OVERTIME.getStatus());
        } else if (showType.equals(5)) {
            // 已取消订单
            status.add(STATUS_USER_CANCEL.getStatus());
            status.add(STATUS_AUTO_CANCEL.getStatus());
            status.add(STATUS_ADMIN_CANCEL.getStatus());
            status.add(STATUS_BRAND_CANCEL.getStatus());
            status.add(STATUS_REFUND.getStatus());
        } else {
            return null;
        }
        return status;
    }

    public static List<Short> brandOrderStatus(Integer showType) {
        // 全部订单
        if (showType.equals(0)) {
            return null;
        }
        List<Short> status = new ArrayList<>();
        if (showType.equals(1)) {
            status.add(STATUS_REFUND.getStatus());
            status.add(STATUS_GROUPON_FAIL.getStatus());
        } else if (showType.equals(2)) {
            // 待发货订单
            status.add(STATUS_PAY.getStatus());
            status.add(STATUS_BTL_PAY.getStatus());
            status.add(STATUS_GROUPON_SUCCEED.getStatus());
        } else if (showType.equals(3)) {
            // 待收货订单
            status.add(STATUS_SHIP.getStatus());
            status.add(STATUS_CONFIRM.getStatus());
            status.add(STATUS_AUTO_CONFIRM.getStatus());
            status.add(ORDER_SUCCEED.getStatus());
            status.add(STATUS_COMMENT_OVERTIME.getStatus());
        }  else if (showType.equals(4)) {
            // 售后订单
            status.add(STATUS_PUT_AFTERSALE.getStatus());
            status.add(STATUS_DISPOSE_AFTERSALE.getStatus());
            status.add(STATUS_FINISH_AFTERSALE.getStatus());
            status.add(STATUS_REJECT_AFTERSALE.getStatus());
        }else {
            return null;
        }
        return status;
    }

    /**判断是否已支付*/
    public static boolean hasPayed(LitemallOrder order) {
        return  OrderStatus.isPayStatus(order) || OrderStatus.isBtlPayStatus(order);
    }

    /**判断是否未支付*/
    public static boolean isPayed(LitemallOrder order) {
        return OrderStatus.isCreateStatus(order) || OrderStatus.isGrouponNoneStatus(order);
    }

    /**判断是否退款*/
    public static boolean isRefund(LitemallOrder order) {
        return OrderStatus.isRefundConfirmStatus(order) || OrderStatus.isFinishAftersaleStatus(order);
    }

    /**判断是否可以删除 ， 如果订单不是关闭状态(已取消、系统取消、已退款、用户已确认、系统已确认)，则不能删除*/
    public static boolean hasDelete(LitemallOrder order) {
        return OrderStatus.isCancelStatus(order) || OrderStatus.isAutoCancelStatus(order) ||
                OrderStatus.isConfirmStatus(order) || OrderStatus.isAutoConfirmStatus(order) ||
                OrderStatus.isRefundConfirmStatus(order);
    }

    /**判断是否可以发货*/
    public static boolean hasShip(LitemallOrder order) {
        return OrderStatus.isPayStatus(order) || OrderStatus.isBtlPayStatus(order)
                || OrderStatus.isGrouponSucceedStatus(order);
    }

    /**未付款*/
    public static boolean isCreateStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_CREATE.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已取消*/
    public static boolean isCancelStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_USER_CANCEL.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已取消（系统）*/
    public static boolean isAutoCancelStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_AUTO_CANCEL.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已付款*/
    public static boolean isPayStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_PAY.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**线下付款*/
    public static boolean isBtlPayStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_BTL_PAY.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**订单取消，退款中*/
    public static boolean isRefundStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_REFUND.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已退款*/
    public static boolean isRefundConfirmStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_REFUND_CONFIRM.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**待开团（未支付）*/
    public static boolean isGrouponNoneStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_GROUPON_NONE.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**团购中（已支付）*/
    public static boolean isGrouponOnStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_GROUPON_ON.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**团购失败（待退款）*/
    public static boolean isGrouponFailStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_GROUPON_FAIL.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**团购成功（待发货）*/
    public static boolean isGrouponSucceedStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_GROUPON_SUCCEED.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已发货*/
    public static boolean isShipStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_SHIP.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已收货*/
    public static boolean isConfirmStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_CONFIRM.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**已收货（系统）*/
    public static boolean isAutoConfirmStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_AUTO_CONFIRM.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**评论已超时*/
    public static boolean isCommentOvertimeStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_COMMENT_OVERTIME.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**交易完成*/
    public static boolean isOrderSucceedStatus(LitemallOrder litemallOrder) {
        return OrderStatus.ORDER_SUCCEED.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**售后申请中*/
    public static boolean isPutAftersaleStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_PUT_AFTERSALE.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**售后退款中*/
    public static boolean isDisposeAftersaleStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_DISPOSE_AFTERSALE.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**售后已退款*/
    public static boolean isFinishAftersaleStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_FINISH_AFTERSALE.getStatus().equals(litemallOrder.getOrderStatus());
    }
    /**售后已拒绝*/
    public static boolean isRejectAftersaleStatus(LitemallOrder litemallOrder) {
        return OrderStatus.STATUS_REJECT_AFTERSALE.getStatus().equals(litemallOrder.getOrderStatus());
    }


}
