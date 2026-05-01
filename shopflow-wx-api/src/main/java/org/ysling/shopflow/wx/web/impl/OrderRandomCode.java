package org.ysling.shopflow.wx.web.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.utils.RandomStrUtil;
import org.ysling.shopflow.wx.service.WxAftersaleService;
import org.ysling.shopflow.wx.service.WxDealingSlipService;
import org.ysling.shopflow.wx.service.WxOrderService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 编号生成
 * @author Ysling
 */
@Service
public class OrderRandomCode {

    @Autowired
    private WxOrderService orderService;
    @Autowired
    private WxAftersaleService aftersaleService;
    @Autowired
    private WxDealingSlipService dealingSlipService;


    /**
     * 生成售后编号
     */
    public String generateAftersaleSn(String userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        LocalDateTime localDateTime = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        String aftersaleSn = df.format(localDateTime);
        while (aftersaleService.countByAftersaleSn(userId, aftersaleSn)) {
            aftersaleSn = df.format(localDateTime);
        }
        return aftersaleSn;
    }

    /**
     * 生成订单编号
     */
    public String generateOrderSn(String userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        LocalDateTime localDateTime = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        String orderSn = df.format(localDateTime);
        while (orderService.countByOrderSn(userId, orderSn)) {
            orderSn = df.format(localDateTime);
        }
        return orderSn;
    }

    /**
     * 生成商户订单号
     */
    public String generateOutTradeNo(String userId) {
        String outTradeNo = RandomStrUtil.getRandom(32 ,RandomStrUtil.TYPE.NUMBER , true);
        while (orderService.countByOutTradeNo(userId, outTradeNo)){
            outTradeNo = RandomStrUtil.getRandom(32 ,RandomStrUtil.TYPE.NUMBER , true);
        }
        return outTradeNo;
    }

    /**
     * 生成转账批次单号
     */
    public String generateOutBatchNo(String userId) {
        String outBatchNo = RandomStrUtil.getRandom(32 ,RandomStrUtil.TYPE.NUMBER , true);
        while (dealingSlipService.isByOutBatchNo(userId, outBatchNo)){
            outBatchNo = RandomStrUtil.getRandom(32 ,RandomStrUtil.TYPE.NUMBER , true);
        }
        return outBatchNo;
    }

}
