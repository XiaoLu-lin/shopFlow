package org.ysling.litemall.core.weixin.service;
// Copyright (c) [ysling] [927069313@qq.com]
// [litemall-plus] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.service.CommonService;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.core.weixin.request.GoodsDetail;
import org.ysling.litemall.core.weixin.request.WxLogisticsRequest;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallOrderGoods;
import org.ysling.litemall.db.domain.LitemallUser;
import org.ysling.litemall.db.service.IUserService;
import java.util.*;

/**
 * 微信物流查询
 *
 * 描述：商户使用此接口向微信提供某交易单号对应的运单号。微信后台会跟踪运单的状态变化
 * 请求方法： POST application/json
 * 请求地址：https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/trace_waybill?access_token=XXX
 * @author Ysling
 */
@Slf4j
@Service
public class LogisticsPluginService {

    /**微信物流插件请求地址*/
    private final static String REQ_URL = "https://api.weixin.qq.com/cgi-bin/express/delivery/open_msg/trace_waybill";

    @Autowired
    private WxMaService wxService;
    @Autowired
    private IUserService userService;
    @Autowired
    private CommonService commonService;

    /**
     * 获取微信物流查询插件token
     * @param userId 用户id
     * @param orderSn 订单编号
     * @return waybillToken
     */
    public Object getWaybillToken(String userId, String orderSn) throws WxErrorException {
        if (userId == null){
            return ResponseUtil.unlogin();
        }

        if (orderSn == null){
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = commonService.findBySn(userId, orderSn);
        if (order == null){
            return ResponseUtil.fail("未找到订单,请重试");
        }

        if (!StringUtils.hasText(order.getShipSn())){
            return ResponseUtil.fail("订单未发货");
        }

        LitemallUser user = userService.findById(userId);
        if (user == null){
            return ResponseUtil.fail("用户信息查找失败请重试");
        }

        //物流请求参数
        WxLogisticsRequest request = new WxLogisticsRequest();
        request.setOpenid(user.getOpenid());
        request.setWaybillId(order.getShipSn());
        request.setReceiverPhone(order.getMobile());
        request.setTransId(order.getPayId());

        //物流商品信息
        List<GoodsDetail> detailList = new ArrayList<>();
        List<LitemallOrderGoods> goodsList = commonService.queryByOid(order.getId());
        for (LitemallOrderGoods orderGoods :goodsList) {
            GoodsDetail goodsDetail = new GoodsDetail();
            goodsDetail.setGoodsName(orderGoods.getGoodsName());
            goodsDetail.setGoodsImgUrl(orderGoods.getPicUrl());
            goodsDetail.setGoodsDesc(Arrays.toString(orderGoods.getSpecifications()));
            detailList.add(goodsDetail);
        }

        Map<String, List<GoodsDetail>> goodsInfo = new HashMap<>();
        goodsInfo.put("detail_list" , detailList);
        request.setGoodsInfo(goodsInfo);
        //将请求参数转为json
        String requestData = JacksonUtil.toJson(request);
        if (requestData == null){
            throw new RuntimeException("物流信息参数错误");
        }

        //获取小程序全局唯一后台接口调用凭据
        String accessToken = wxService.getAccessToken();
        String result = HttpUtil.post(REQ_URL + "?access_token=" + accessToken, requestData);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String waybillToken = jsonObject.getStr("waybill_token");
        if (!StringUtils.hasText(waybillToken)){
            return ResponseUtil.fail("微信物流查询插件调用失败");
        }

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("waybillToken" ,waybillToken);
        return ResponseUtil.ok(dataMap);
    }


}
