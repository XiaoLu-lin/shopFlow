package org.ysling.litemall.wx.web.impl;
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

import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.service.AftersaleCoreService;
import org.ysling.litemall.core.service.NotifyCoreService;
import org.ysling.litemall.core.service.OrderCoreService;
import org.ysling.litemall.core.tasks.impl.OrderCommentTask;
import org.ysling.litemall.core.tasks.service.TaskService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.core.weixin.service.SubscribeMessageService;
import org.ysling.litemall.core.weixin.service.WxPayRefundService;
import org.ysling.litemall.core.weixin.service.WxSecCheckService;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.entity.OrderHandleOption;
import org.ysling.litemall.db.enums.AftersaleStatus;
import org.ysling.litemall.db.enums.OrderStatus;
import org.ysling.litemall.wx.model.aftersale.body.AftersaleListBody;
import org.ysling.litemall.wx.model.aftersale.result.AftersaleDetailResult;
import org.ysling.litemall.wx.model.aftersale.result.AftersaleListResult;
import org.ysling.litemall.wx.service.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 售后服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebAftersaleService {

    @Autowired
    private WxSecCheckService secCheckService;
    @Autowired
    private AftersaleCoreService aftersaleCoreService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;
    @Autowired
    private OrderRandomCode orderRandomCode;
    @Autowired
    private WxAftersaleService aftersaleService;
    @Autowired
    private WxOrderService orderService;
    @Autowired
    private WxOrderGoodsService orderGoodsService;
    @Autowired
    private WxPayRefundService wxPayRefundService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private WxUserService userService;
    @Autowired
    private NotifyCoreService notifyCoreService;
    @Autowired
    private WxBrandService brandService;
    @Autowired
    private OrderCoreService orderCoreService;

    /**
     * 售后列表
     */
    public Object list(String userId, AftersaleListBody body) {
        List<LitemallAftersale> aftersaleList = aftersaleService.querySelective(userId, body);
        List<AftersaleListResult> aftersaleVoList = new ArrayList<>(aftersaleList.size());
        for (LitemallAftersale aftersale : aftersaleList) {
            AftersaleListResult result = new AftersaleListResult();
            result.setAftersale(aftersale);
            result.setStatusText(AftersaleStatus.parseValue(aftersale.getStatus()));
            result.setOrderGoods(orderGoodsService.findByOrderId(aftersale.getOrderId()));
            aftersaleVoList.add(result);
        }
        return ResponseUtil.okList(aftersaleVoList, aftersaleList);
    }

    /**
     * 售后详情
     *
     * @param orderId 订单ID
     * @return 售后详情
     */
    public Object detail(String userId, String orderId) {
        // 订单信息
        LitemallOrder order = orderService.findById(userId, orderId);
        if (order == null) {
            LitemallBrand brand = brandService.findByUserId(userId);
            if (brand == null){
                return ResponseUtil.fail("订单不存在");
            }
            order = orderService.findByBrandId(brand.getId() , orderId);
            if (order == null){
                return ResponseUtil.fail( "订单不存在");
            }
        }

        LitemallOrderGoods orderGoods = orderGoodsService.findByOrderId(orderId);
        if (orderGoods == null){
            return ResponseUtil.fail( "订单不存在");
        }

        LitemallAftersale aftersale = aftersaleService.findByOrderId(order.getUserId(), orderId);
        if (aftersale == null){
            return ResponseUtil.fail("无售后记录");
        }

        AftersaleDetailResult result = new AftersaleDetailResult();
        result.setOrder(order);
        result.setAftersale(aftersale);
        result.setOrderGoods(orderGoods);
        return ResponseUtil.ok(result);
    }

    /**
     * 申请售后
     *
     * @param userId   用户ID
     * @param aftersale 用户售后信息
     * @return 操作结果
     */
    public Object submit(String userId, LitemallAftersale aftersale) {
        Object error = validate(aftersale);
        if (error != null) {
            return error;
        }
        // 进一步验证
        String orderId = aftersale.getOrderId();
        if(Objects.isNull(orderId)){
            return ResponseUtil.badArgument();
        }

        //文本校验
        LitemallUser user = userService.findById(userId);
        secCheckService.checkMessage(user.getOpenid(), aftersale.toString());

        LitemallOrder order = orderService.findById(userId, orderId);
        if(Objects.isNull(order)){
            return ResponseUtil.badArgumentValue();
        }

        // 订单必须完成才能进入售后流程。
        OrderHandleOption handleOption = OrderStatus.build(order);
        if(!handleOption.isAftersale()){
            return ResponseUtil.fail( "不能申请售后");
        }

        BigDecimal amount = order.getActualPrice().subtract(order.getFreightPrice());
        if(aftersale.getAmount().compareTo(amount) > 0){
            return ResponseUtil.fail( "退款金额不正确");
        }

        Short afterStatus = order.getAftersaleStatus();
        if(afterStatus.equals(AftersaleStatus.STATUS_RECEPT.getStatus()) || afterStatus.equals(AftersaleStatus.STATUS_REFUND.getStatus())){
            return ResponseUtil.fail( "已申请售后");
        }

        // 如果有旧的售后记录则删除（例如用户已取消，管理员拒绝）
        aftersaleService.deleteByOrderId(userId, orderId);
        aftersale.setStatus(AftersaleStatus.STATUS_REQUEST.getStatus());
        aftersale.setAftersaleSn(orderRandomCode.generateAftersaleSn(userId));
        aftersale.setUserId(userId);
        aftersaleService.add(aftersale);

        //修改订单状态
        order.setOrderStatus(OrderStatus.STATUS_PUT_AFTERSALE.getStatus());
        // 订单的aftersale_status和售后记录的status是一致的。
        order.setAftersaleStatus(AftersaleStatus.STATUS_REQUEST.getStatus());
        if (orderService.updateVersionSelective(order) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //删除评论超时定时任务
        taskService.removeTask(new OrderCommentTask(order));

        //给商家发送通知
        notifyCoreService.aftersaleRefundNotify(order , aftersale);
        return ResponseUtil.ok();
    }

    /**
     * 取消售后
     *
     * 如果管理员还没有审核，用户可以取消自己的售后申请
     *
     * @param userId   用户ID
     * @param aftersale 用户售后信息
     * @return 操作结果
     */
    public Object cancel(String userId, LitemallAftersale aftersale) {
        String id = aftersale.getId();
        if(id == null){
            return ResponseUtil.badArgument();
        }
        LitemallAftersale aftersaleOne = aftersaleService.findById(userId, id);
        if(aftersaleOne == null){
            return ResponseUtil.badArgument();
        }

        String orderId = aftersaleOne.getOrderId();
        LitemallOrder order = orderService.findById(userId, orderId);
        if(!order.getUserId().equals(userId)){
            return ResponseUtil.badArgumentValue();
        }

        // 订单必须完成才能进入售后流程。
        if(!OrderStatus.isPutAftersaleStatus(order)){
            return ResponseUtil.fail( "不能取消售后");
        }

        Short afterStatus = order.getAftersaleStatus();
        if(!afterStatus.equals(AftersaleStatus.STATUS_REQUEST.getStatus())){
            return ResponseUtil.fail( "不能取消售后");
        }

        aftersale.setUserId(userId);
        aftersale.setStatus(AftersaleStatus.STATUS_CANCEL.getStatus());
        if (aftersaleService.updateVersionSelective(aftersale) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        // 订单的aftersale_status和售后记录的status是一致的。
        orderService.updateAftersaleStatus(orderId, AftersaleStatus.STATUS_CANCEL.getStatus());
        return ResponseUtil.ok();
    }

    /**
     * 审核通过
     */
    public Object recept(LitemallAftersale aftersale) {
        LitemallOrder order = orderService.findById(aftersale.getOrderId());
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        aftersale = aftersaleService.findByOrderId(order.getUserId(), order.getId());
        if(aftersale == null){
            return ResponseUtil.fail("售后不存在");
        }
        return aftersaleCoreService.recept(aftersale);
    }


    /**
     * 审核驳回
     */
    public Object reject(LitemallAftersale aftersale) {
        LitemallOrder order = orderService.findById(aftersale.getOrderId());
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        aftersale = aftersaleService.findByOrderId(order.getUserId(),order.getId());
        if(aftersale == null){
            return ResponseUtil.fail("售后不存在");
        }
        return aftersaleCoreService.reject(aftersale);
    }

    /**
     * 售后退款
     */
    public Object refund(LitemallAftersale aftersale) {
        return aftersaleCoreService.refund(aftersale.getId());
    }


    private Object validate(LitemallAftersale aftersale) {
        if (aftersale == null) {
            return ResponseUtil.badArgument();
        }
        Short type = aftersale.getType();
        if (type == null) {
            return ResponseUtil.badArgument();
        }
        BigDecimal amount = aftersale.getAmount();
        if (amount == null) {
            return ResponseUtil.badArgument();
        }
        String reason = aftersale.getReason();
        if (Objects.isNull(reason)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
}