package org.ysling.litemall.core.express.service;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.express.config.ExpressProperties;
import org.ysling.litemall.core.express.model.ExpressInfo;
import org.ysling.litemall.core.express.request.ExpressRequest;
import org.ysling.litemall.core.utils.HttpUtil;
import org.springframework.util.Base64Utils;
import org.ysling.litemall.core.utils.JacksonUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流查询服务
 * <p>
 * 快递鸟即时查询API http://www.kdniao.com/api-track
 * @author Ysling
 */
@Slf4j
@Service
public class ExpressService {

    private final ExpressProperties properties;

    public ExpressService(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取物流信息
     *
     * @param expCode   物流公司编号
     * @param expNo     物流单号
     * @return 物流信息
     */
    public ExpressInfo getExpressInfo(String expCode, String expNo) {
        if (!properties.isEnable()) {
            return null;
        }
        try {
            String result = getOrderTracesByJson(expCode, expNo);
            ObjectMapper objMap = new ObjectMapper();
            ExpressInfo info = objMap.readValue(result, ExpressInfo.class);
            if (!info.isSuccess()){
                throw new RuntimeException(info.getReason());
            }
            info.setShipperName(getVendorName(expCode));
            //将物流轨迹倒序排序
            Collections.reverse(info.getTraces());
            return info;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取物流供应商名
     *
     * @param vendorCode 物流公司编号
     * @return           物流公司名称
     */
    public String getVendorName(String vendorCode) {
        for (Map<String, String> item : properties.getVendors()) {
            if (item.get("code").equals(vendorCode)) {
                return item.get("name");
            }
        }
        return null;
    }

    /**
     * 查询支持的物流公司
     * @return  查询支持的物流公司
     */
    public List<Map<String, String>> getVendors() {
        return properties.getVendors();
    }

    /**
     *
     * @param expCode   物流公司编号
     * @param expNo     物流单号
     * @return          物流信息
     * @throws Exception 异常
     */
    private String getOrderTracesByJson(String expCode, String expNo) throws Exception {
        //生成请求方法
        ExpressRequest request = new ExpressRequest();
        request.setShipperCode(expCode);
        request.setLogisticCode(expNo);
        String requestData = JacksonUtil.toJson(request);
        if (requestData == null){
            throw new RuntimeException("物流信息参数错误");
        }
        //生成请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
        params.put("EBusinessID", properties.getAppId());
        params.put("RequestType", "1002");
        String dataSign = encrypt(requestData, properties.getAppKey());
        params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
        params.put("DataType", "2");
        //请求url
        String reqURL = "https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
        return HttpUtil.sendPost(reqURL, params);
    }

    /**
     * MD5加密
     *
     * @param str     内容
     * @throws Exception 异常
     */
    private String MD5(String str) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(StandardCharsets.UTF_8));
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder(32);
        for (byte b : result) {
            int val = b & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Sign签名生成
     *
     * @param content  内容
     * @param keyValue AppKey
     * @return DataSign签名
     */
    private String encrypt(String content, String keyValue) throws Exception {
        if (keyValue != null) {
            content = content + keyValue;
        }
        byte[] src = MD5(content).getBytes(StandardCharsets.UTF_8);
        return Base64Utils.encodeToString(src);
    }
}
