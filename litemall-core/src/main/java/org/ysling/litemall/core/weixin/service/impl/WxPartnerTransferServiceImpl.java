package org.ysling.litemall.core.weixin.service.impl;
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
import com.github.binarywang.wxpay.bean.marketing.transfer.*;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.PartnerTransferServiceImpl;
import com.github.binarywang.wxpay.v3.util.RsaCryptoUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author Ysling
 */
public class WxPartnerTransferServiceImpl extends PartnerTransferServiceImpl {

    private static final Gson GSON = (new GsonBuilder()).create();

    private final WxPayService payService;

    public WxPartnerTransferServiceImpl(WxPayService payService) {
        super(payService);
        this.payService = payService;
    }

    @Override
    public PartnerTransferResult batchTransfer(PartnerTransferRequest request) throws WxPayException {
        request.getTransferDetailList().forEach((p) -> {
            try {
                if (StringUtils.hasText(p.getUserName())){
                    String userName = RsaCryptoUtil.encryptOAEP(p.getUserName(), payService.getConfig().getVerifier().getValidCertificate());
                    p.setUserName(userName);
                }
            } catch (IllegalBlockSizeException var3) {
                throw new RuntimeException("姓名转换异常!", var3);
            }
        });
        String url = String.format("%s/v3/transfer/batches", this.payService.getPayBaseUrl());
        String response = this.payService.postV3WithWechatpaySerial(url, GSON.toJson(request));
        return GSON.fromJson(response, PartnerTransferResult.class);
    }

    @Override
    public BatchNumberResult queryBatchByBatchId(BatchNumberRequest request) throws WxPayException {
        String url = String.format("%s/v3/transfer/batches/batch-id/%s", this.payService.getPayBaseUrl(), request.getBatchId());
        if (request.getOffset() == null) {
            request.setOffset(0);
        }
        if (request.getLimit() == null || request.getLimit() <= 0) {
            request.setLimit(20);
        }
        if (request.getNeedQueryDetail() == null){
            request.setNeedQueryDetail(true);
        }
        if (request.getDetailStatus() == null){
            request.setDetailStatus("ALL");
        }
        String query = String.format("?need_query_detail=%s&offset=%s&limit=%s",
                request.getNeedQueryDetail(),
                request.getOffset(),
                request.getLimit());
        if (StringUtil.isNotBlank(request.getDetailStatus())) {
            query = query + "&detail_status=" + request.getDetailStatus();
        }
        String response = this.payService.getV3(url + query);
        return GSON.fromJson(response, BatchNumberResult.class);
    }

    @Override
    public BatchNumberResult queryBatchByOutBatchNo(MerchantBatchRequest request) throws WxPayException {
        String url = String.format("%s/v3/transfer/batches/out-batch-no/%s", this.payService.getPayBaseUrl(), request.getOutBatchNo());
        if (request.getOffset() == null) {
            request.setOffset(0);
        }
        if (request.getLimit() == null || request.getLimit() <= 0) {
            request.setLimit(20);
        }
        if (request.getNeedQueryDetail() == null){
            request.setNeedQueryDetail(true);
        }
        if (request.getDetailStatus() == null){
            request.setDetailStatus("ALL");
        }
        String query = String.format("?need_query_detail=%s&offset=%s&limit=%s",
                request.getNeedQueryDetail(),
                request.getOffset(),
                request.getLimit());
        if (StringUtil.isNotBlank(request.getDetailStatus())) {
            query = query + "&detail_status=" + request.getDetailStatus();
        }
        String response = this.payService.getV3(url + query);
        return GSON.fromJson(response, BatchNumberResult.class);
    }


    @Override
    public BatchDetailsResult queryBatchDetailByWeChat(String batchId, String detailId) throws WxPayException, BadPaddingException {
        String url = String.format("%s/v3/transfer/batches/batch-id/%s/details/detail-id/%s", this.payService.getPayBaseUrl(), batchId, detailId);
        String response = this.payService.getV3(url);
        BatchDetailsResult batchDetailsResult = GSON.fromJson(response, BatchDetailsResult.class);
        if (batchDetailsResult.getUserName() != null){
            String userName = RsaCryptoUtil.decryptOAEP(batchDetailsResult.getUserName(), this.payService.getConfig().getPrivateKey());
            batchDetailsResult.setUserName(userName);
        }
        return batchDetailsResult;
    }


    @Override
    public BatchDetailsResult queryBatchDetailByMch(String outBatchNo, String outDetailNo) throws WxPayException, BadPaddingException {
        String url = String.format("%s/v3/transfer/batches/out-batch-no/%s/details/out-detail-no/%s", this.payService.getPayBaseUrl(), outBatchNo, outDetailNo);
        String response = this.payService.getV3(url);
        BatchDetailsResult batchDetailsResult = GSON.fromJson(response, BatchDetailsResult.class);
        if (batchDetailsResult.getUserName() != null){
            String userName = RsaCryptoUtil.decryptOAEP(batchDetailsResult.getUserName(), this.payService.getConfig().getPrivateKey());
            batchDetailsResult.setUserName(userName);
        }
        return batchDetailsResult;
    }

}
