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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.marketing.transfer.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.PartnerTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.notify.service.NotifyMailService;
import org.ysling.litemall.core.tasks.impl.DealingSlipMonitorTask;
import org.ysling.litemall.core.tasks.service.TaskService;
import org.ysling.litemall.core.weixin.request.WxPartnerTransferRequest;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.service.IDealingSlipService;
import javax.crypto.BadPaddingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ysling
 */
@Slf4j
@Service
public class WxPayTransferService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private WxPayConfig wxPayConfig;
    @Autowired
    private NotifyMailService mailService;
    @Autowired
    private IDealingSlipService dealingSlipService;
    @Autowired
    private PartnerTransferService transferService;

    /**
     * 余额提现
     * @param openid 用户信息
     * @param cashAmountPrice 转账金额
     * @param outBatchNo 转账批次单号
     */
    public void withdrawDeposit(String openid, String trueName, BigDecimal cashAmountPrice, String outBatchNo) {
        //转账金额元转分
        Integer totalAmount = cashAmountPrice.multiply(BigDecimal.valueOf(100)).intValue();
        //转账明细列表
        List<PartnerTransferRequest.TransferDetail> transferDetailList = new ArrayList<>();
        //订单转账明细
        PartnerTransferRequest.TransferDetail orderTransferDetail = new PartnerTransferRequest.TransferDetail();
        //转账金额
        orderTransferDetail.setTransferAmount(totalAmount);
        //用户openId
        orderTransferDetail.setOpenid(openid);
        //转账备注
        orderTransferDetail.setTransferRemark("用户余额提现");
        //转账批次单号
        orderTransferDetail.setOutDetailNo(outBatchNo);
        //真实姓名
        orderTransferDetail.setUserName(trueName);
        //添加进转账明细列表
        transferDetailList.add(orderTransferDetail);

        //订单请求参数
        WxPartnerTransferRequest transferRequest = new WxPartnerTransferRequest();
        //将转账明细列表添加进转账参数
        transferRequest.setTransferDetailList(transferDetailList);
        //小程序appid
        transferRequest.setAppid(wxPayConfig.getAppId());
        //商家批次单号
        transferRequest.setOutBatchNo(outBatchNo);
        //批次名称
        transferRequest.setBatchName("用户余额提现");
        //转账总金额
        transferRequest.setTotalAmount(totalAmount);
        //转账总笔数
        transferRequest.setTotalNum(transferDetailList.size());
        //批次备注
        transferRequest.setBatchRemark("用户余额提现");
        //返回请求返回参数
        try {
            PartnerTransferResult result = transferService.batchTransfer(transferRequest);
            LitemallDealingSlip dealingSlip = findByOutBatchNo(openid, outBatchNo);
            if (dealingSlip != null){
                //加入延时任务五分钟后检查一次
                dealingSlip.setBatchId(result.getBatchId());
                taskService.addTask(new DealingSlipMonitorTask(dealingSlip));
            }
        }catch (WxPayException e){
            throw wxError(e);
        }
    }

    /**
     * 判断outBatchNo是否存在
     * @param openid  用户openid
     * @param outBatchNo 转账单号
     * @return true
     */
    public LitemallDealingSlip findByOutBatchNo(String openid, String outBatchNo) {
        QueryWrapper<LitemallDealingSlip> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallDealingSlip.OPENID , openid);
        wrapper.eq(LitemallDealingSlip.OUT_BATCH_NO , outBatchNo);
        return dealingSlipService.getOne(wrapper);
    }

    /**
     * 转账交易订单查询
     * @param batchId 微信转账批次单号
     */
    public BatchNumberResult queryBatchByBatchId(String batchId) {
        BatchNumberRequest request = new BatchNumberRequest();
        request.setBatchId(batchId);
        //返回请求返回参数
        try {
            return transferService.queryBatchByBatchId(request);
        } catch (WxPayException e) {
            throw wxError(e);
        }
    }

    /**
     * 转账交易订单查询
     * @param outBatchNo 自己的转账批次单号
     */
    public BatchNumberResult queryBatchByOutBatchNo(String outBatchNo) {
        MerchantBatchRequest request = new MerchantBatchRequest();
        request.setOutBatchNo(outBatchNo);
        //返回请求返回参数
        try {
            return transferService.queryBatchByOutBatchNo(request);
        } catch (WxPayException e) {
            throw wxError(e);
        }
    }


    /**
     * 转账交易订单查询
     * @param batchId 微信的转账批次单号
     * @param detailId 微信明细单号
     */
    public BatchDetailsResult queryBatchDetailByWeChat(String batchId, String detailId) {
        try {
            return transferService.queryBatchDetailByWeChat(batchId , detailId);
        } catch (WxPayException e) {
            throw wxError(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转账交易订单查询
     * @param outBatchNo 自己的转账批次单号
     * @param outDetailNo 微信明细单号
     */
    public BatchDetailsResult queryBatchDetailByMch(String outBatchNo, String outDetailNo) {
        try {
            return transferService.queryBatchDetailByMch(outBatchNo , outDetailNo);
        } catch (WxPayException e) {
            throw wxError(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 通用处理WxPayException
     */
    private RuntimeException wxError(WxPayException e) {
        log.error(e.getMessage(), e);
        //返回错误信息
        if (StringUtils.hasText(e.getErrCodeDes())){
            //给管理员发送订单支付错误邮件
            mailService.notifyMail("商家付款到零钱异常", e.getErrCodeDes());
            throw new RuntimeException(e.getErrCodeDes());
        }if (StringUtils.hasText(e.getCustomErrorMsg())){
            //给管理员发送订单支付错误邮件
            mailService.notifyMail("商家付款到零钱异常", e.getCustomErrorMsg());
            throw new RuntimeException(e.getCustomErrorMsg());
        }else {
            //给管理员发送订单支付错误邮件
            mailService.notifyMail("商家付款到零钱异常", e.getMessage());
            throw new RuntimeException("商家付款失败，请稍后重试");
        }
    }

}
