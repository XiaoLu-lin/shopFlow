package org.ysling.shopflow.core.service;

import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.handler.ActionLogHandler;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.weixin.service.SubscribeMessageService;
import org.ysling.shopflow.core.weixin.service.WxPayRefundService;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.service.IAftersaleService;
import org.ysling.shopflow.db.service.IOrderService;
import org.ysling.shopflow.db.domain.ShopflowAftersale;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.enums.AftersaleStatus;
import org.ysling.shopflow.db.enums.OrderStatus;
import org.ysling.shopflow.db.service.IUserService;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后服务类
 * @author Ysling
 */
@Service
public class AftersaleCoreService {


    @Autowired
    private CommonService commonService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IAftersaleService aftersaleService;
    @Autowired
    private IUserService userService;
    @Autowired
    private OrderCoreService orderCoreService;
    @Autowired
    private WxPayRefundService wxPayRefundService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;


    /**
     * 售后审核通过
     * @param aftersale 售后信息
     * @return 成功
     */
    public Object recept(ShopflowAftersale aftersale) {
        //判断售后状态
        if(!aftersale.getStatus().equals(AftersaleStatus.STATUS_REQUEST.getStatus())){
            throw new RuntimeException("当前售后状态不能进行审核通过操作");
        }

        ShopflowOrder order = commonService.findOrderById(aftersale.getUserId(), aftersale.getOrderId());
        if (order == null) {
            throw new RuntimeException("未找到售后订单="+aftersale.getOrderId());
        }

        aftersale.setStatus(AftersaleStatus.STATUS_RECEPT.getStatus());
        aftersale.setHandleTime(LocalDateTime.now());
        if (aftersaleService.updateVersionSelective(aftersale) == 0) {
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //订单也要更新售后状态
        order.setAftersaleStatus(AftersaleStatus.STATUS_RECEPT.getStatus());
        order.setOrderStatus(OrderStatus.STATUS_DISPOSE_AFTERSALE.getStatus());
        if (orderService.updateVersionSelective(order) == 0) {
            throw new RuntimeException("网络繁忙，请刷新重试");
        }
        return ResponseUtil.ok();
    }

    /**
     * 售后审核拒绝
     * @param aftersale 售后
     * @return 成功
     */
    public Object reject(ShopflowAftersale aftersale) {
        //判断售后状态
        if(!aftersale.getStatus().equals(AftersaleStatus.STATUS_REQUEST.getStatus())){
            throw new RuntimeException("当前售后状态不能进行审核拒绝操作");
        }

        ShopflowOrder order = commonService.findOrderById(aftersale.getUserId(), aftersale.getOrderId());
        if (order == null) {
            throw new RuntimeException("未找到售后订单="+aftersale.getOrderId());
        }

        aftersale.setStatus(AftersaleStatus.STATUS_REJECT.getStatus());
        aftersale.setHandleTime(LocalDateTime.now());
        if (aftersaleService.updateVersionSelective(aftersale) == 0) {
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //订单也要更新售后状态
        order.setAftersaleStatus(AftersaleStatus.STATUS_REJECT.getStatus());
        order.setOrderStatus(OrderStatus.STATUS_REJECT_AFTERSALE.getStatus());
        if (orderService.updateVersionSelective(order) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }
        return ResponseUtil.ok();
    }


    /**
     * 售后退款
     * @param aftersaleId 售后ID
     */
    public Object refund(String aftersaleId) {
        ShopflowAftersale aftersale = aftersaleService.findById(aftersaleId);
        if(aftersale == null){
            return ResponseUtil.fail("未找到售后申请");
        }

        if(!aftersale.getStatus().equals(AftersaleStatus.STATUS_RECEPT.getStatus())){
            return ResponseUtil.fail("售后不能进行退款操作");
        }

        ShopflowOrder order = orderService.findById(aftersale.getOrderId());
        if (order == null) {
            return ResponseUtil.fail("未找到售后订单");
        }

        //如果订单金额为零则跳过退款接口直接修改订单状态
        if (order.getActualPrice().compareTo(BigDecimal.ZERO) > 0) {
            WxPayRefundResult refundResult = wxPayRefundService.wxPayAftersaleRefund(order , aftersale);
            if (refundResult != null) {
                order.setRefundContent(refundResult.getRefundId());
            }
        } else {
            order.setRefundContent("非微信支付退款");
        }

        //修改售后信息
        aftersale.setStatus(AftersaleStatus.STATUS_REFUND.getStatus());
        aftersale.setHandleTime(LocalDateTime.now());
        if (aftersaleService.updateVersionSelective(aftersale) == 0) {
            return ResponseUtil.fail("售后更新失败");
        }

        // 记录订单退款相关信息
        order.setRefundType("微信退款接口");
        order.setRefundTime(LocalDateTime.now());
        order.setRefundAmount(order.getActualPrice());
        order.setAftersaleStatus(AftersaleStatus.STATUS_REFUND.getStatus());
        //修改订单状态
        order.setOrderStatus(OrderStatus.STATUS_FINISH_AFTERSALE.getStatus());
        if (orderService.updateVersionSelective(order) == 0) {
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        // 返还订单
        orderCoreService.orderRelease(order);
        return ResponseUtil.ok();
    }



}
