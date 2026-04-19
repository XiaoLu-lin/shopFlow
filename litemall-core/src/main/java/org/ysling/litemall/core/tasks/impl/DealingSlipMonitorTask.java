package org.ysling.litemall.core.tasks.impl;
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

import com.github.binarywang.wxpay.bean.marketing.transfer.BatchDetailsResult;
import com.github.binarywang.wxpay.bean.marketing.transfer.BatchNumberResult;
import lombok.extern.slf4j.Slf4j;

import org.ysling.litemall.core.handler.ActionLogHandler;
import org.ysling.litemall.core.service.DealingSlipCoreService;
import org.ysling.litemall.core.tasks.service.TaskRunnable;
import org.ysling.litemall.core.utils.BeanUtil;
import org.ysling.litemall.core.weixin.service.WxPayTransferService;
import org.ysling.litemall.db.domain.LitemallDealingSlip;
import org.ysling.litemall.core.weixin.enums.TransferStatus;
import org.ysling.litemall.db.domain.LitemallUser;
import org.ysling.litemall.db.service.IDealingSlipService;
import org.ysling.litemall.db.service.IUserService;
import java.math.BigDecimal;

/**
 * 微信转账监控
 * @author Ysling
 */
@Slf4j
public class DealingSlipMonitorTask extends TaskRunnable {

    /**转账记录ID*/
    private final String dealingSlipId;
    /**转账批次单号*/
    private final String outBatchNo;
    /**转账批次单号*/
    private final String batchId;
    /**id前缀*/
    private static final String ID_PREFIX = "DealingSlipMonitorTask-";
    /**任务名称*/
    private static final String TASK_NAME = "微信转账监控";
    /**默认延时时间 单位毫秒*/
    private static final long DEFAULT_SECONDS = 5 * 60 * 1000;


    public DealingSlipMonitorTask(LitemallDealingSlip dealingSlip){
        super(ID_PREFIX + dealingSlip.getId(), DEFAULT_SECONDS, dealingSlip.getTenantId(), TASK_NAME);
        this.dealingSlipId = dealingSlip.getId();
        this.outBatchNo = dealingSlip.getOutBatchNo();
        this.batchId = dealingSlip.getBatchId();
    }

    public DealingSlipMonitorTask(LitemallDealingSlip dealingSlip, long delayInMilliseconds){
        super(ID_PREFIX + dealingSlip.getId(), delayInMilliseconds, dealingSlip.getTenantId(), TASK_NAME);
        this.dealingSlipId = dealingSlip.getId();
        this.outBatchNo = dealingSlip.getOutBatchNo();
        this.batchId = dealingSlip.getBatchId();
    }

    @Override
    public void runTask() {
        IUserService userService = BeanUtil.getBean(IUserService.class);
        IDealingSlipService dealingSlipService = BeanUtil.getBean(IDealingSlipService.class);
        WxPayTransferService wxPayTransferService = BeanUtil.getBean(WxPayTransferService.class);
        DealingSlipCoreService dealingSlipCoreService = BeanUtil.getBean(DealingSlipCoreService.class);
        LitemallDealingSlip dealingSlip = dealingSlipService.findById(this.dealingSlipId);
        if(dealingSlip == null || !this.outBatchNo.equals(dealingSlip.getOutBatchNo())) {
            return;
        }
        BatchNumberResult result = wxPayTransferService.queryBatchByOutBatchNo(outBatchNo);
        for (BatchNumberResult.TransferDetail detail :result.getTransferDetailList()) {
            String detailStatus = detail.getDetailStatus();
            String outDetailNo = detail.getOutDetailNo();
            //添加转账批次ID
            dealingSlip.setBatchId(batchId);
            dealingSlip.setStatus(detailStatus);
            if (!dealingSlipService.updateById(dealingSlip)){
                throw new RuntimeException("转账记录更新失败");
            }
            //返还提现金额
            if (TransferStatus.isFail(detailStatus)){
                LitemallUser user = userService.findById(dealingSlip.getUserId());
                BatchDetailsResult detailsResult = wxPayTransferService.queryBatchDetailByMch(outBatchNo, outDetailNo);
                BigDecimal award = dealingSlip.getAward().negate();
                dealingSlip.setAward(award);
                dealingSlip.setTenantId(null);
                dealingSlip.setRemark(detailsResult.getFailReason());
                dealingSlip.setBalance(user.getIntegral().add(award));
                dealingSlipCoreService.updateIntegral(user, dealingSlip);
                ActionLogHandler.logOrderFail(String.format("转账结果%s", detailStatus), "outBatchNo:"+this.outBatchNo);
            }
        }
    }

}
