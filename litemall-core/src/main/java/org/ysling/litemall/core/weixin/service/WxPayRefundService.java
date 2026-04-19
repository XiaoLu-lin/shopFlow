package org.ysling.litemall.core.weixin.service;
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


import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.notify.service.NotifyMailService;
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.domain.LitemallOrder;
import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Slf4j
@Service
public class WxPayRefundService {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private NotifyMailService mailService;

    /**
     * 微信退款
     * @param order 订单详情
     */
    public WxPayRefundResult wxPayRefund(LitemallOrder order){
        if (order.getPayId() == null){
            throw new RuntimeException("仅线上支付支持微信退款");
        }
        // 微信退款
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setTransactionId(order.getPayId());
        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
        // 元转成分
        Integer refundFee = order.getActualPrice().multiply(BigDecimal.valueOf(100)).intValue();
        Integer totalFee = order.getOrderPrice().multiply(BigDecimal.valueOf(100)).intValue();
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(refundFee);

        return getWxPayRefundResult(wxPayRefundRequest);
    }


    /**
     * 售后退款
     * @param order 订单详情
     */
    public WxPayRefundResult wxPayAftersaleRefund(LitemallOrder order , LitemallAftersale aftersaleOne){
        if (order.getPayId() == null){
            throw new RuntimeException("仅线上支付支持微信退款");
        }
        // 微信退款
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setTransactionId(order.getPayId());
        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
        // 元转成分
        Integer totalFee = aftersaleOne.getAmount().multiply(BigDecimal.valueOf(100)).intValue();
        wxPayRefundRequest.setTotalFee(order.getActualPrice().multiply(BigDecimal.valueOf(100)).intValue());
        wxPayRefundRequest.setRefundFee(totalFee);

        return getWxPayRefundResult(wxPayRefundRequest);
    }

    /**
     * 调用微信退款接口
     * @param wxPayRefundRequest 请求参数
     * @return WxPayRefundResult
     */
    private WxPayRefundResult getWxPayRefundResult(WxPayRefundRequest wxPayRefundRequest) {
        WxPayRefundResult wxPayRefundResult;
        try {
            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
        } catch (WxPayException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("订单退款失败");
        }
        if (!"SUCCESS".equals(wxPayRefundResult.getReturnCode())) {
            log.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            throw new RuntimeException("订单退款失败");
        }
        if (!"SUCCESS".equals(wxPayRefundResult.getResultCode())) {
            log.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
            throw new RuntimeException("订单退款失败");
        }
        return wxPayRefundResult;
    }


}
