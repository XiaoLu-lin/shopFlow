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
import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.ysling.litemall.core.express.service.ExpressService;
import org.ysling.litemall.db.domain.LitemallMessage;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallOrderGoods;
import org.ysling.litemall.db.enums.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信消息订阅
 * @author Ysling
 */
@Slf4j
@Service
public class SubscribeMessageService {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private ExpressService expressService;
    /**声明需要格式化的格式(日期加时间)*/
    private final DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    /**订单发货提醒订阅模板id*/
    private final static String SHIP_TMPL_ID = "Uy7q5hPRyiL2IRZo22HQ5Je8quwKoL7kzpX3S0SC5q4";
    /**退款订阅模板id*/
    private final static String REFUND_TMPL_ID = "vjTL2TZURvShCeFnjo7Shu0v6D_r9kS3GsvBHcd4tJM";
    /**新订单订阅模板id*/
    private final static String NEW_ORDER_TMPL_ID = "9cgvWe9phZfc4_AgAfFGBJWie0FfrVe3Rae-puLzL2s";
    /**新消息订阅模板id*/
    private final static String NEW_MESSAGE_TMPL_ID = "e_rfKqJ3Da3q8jvABCGsolnQh4b6W-IdIbhgj2369nA";

    /**
     * 订单发货订阅
     * @param openId 用户openId
     * @param order 订单信息
     */
    @Async
    public void shipSubscribe(String openId ,LitemallOrder order){
        // 模板数据 下面的数据，是需要根据申请的微信模板对应的，并不是每个人都一样
        List<WxMaSubscribeMessage.MsgData> data = new ArrayList<>();
        data.add(new WxMaSubscribeMessage.MsgData("character_string1", order.getOrderSn()));
        data.add(new WxMaSubscribeMessage.MsgData("date10", dfDateTime.format(order.getShipTime())));
        data.add(new WxMaSubscribeMessage.MsgData("thing20", expressService.getVendorName(order.getShipChannel())));
        data.add(new WxMaSubscribeMessage.MsgData("character_string4", order.getShipSn()));
        data.add(new WxMaSubscribeMessage.MsgData("thing13", order.getAddress()));
        sendSubscribe(openId , data , SHIP_TMPL_ID);
    }


    /**
     * 订单退货审核订阅通知
     * @param openId 用户openID
     * @param order 订单信息
     */
    @Async
    public void refundSubscribe(String openId ,LitemallOrder order){
        // 模板数据 下面的数据，是需要根据申请的微信模板对应的，并不是每个人都一样
        List<WxMaSubscribeMessage.MsgData> data = new ArrayList<>();
        data.add(new WxMaSubscribeMessage.MsgData("phrase9", OrderStatus.orderStatusText(order)));
        data.add(new WxMaSubscribeMessage.MsgData("character_string2", order.getOrderSn()));
        data.add(new WxMaSubscribeMessage.MsgData("amount3", order.getRefundAmount().toString()));
        data.add(new WxMaSubscribeMessage.MsgData("time11", dfDateTime.format(order.getPayTime())));
        data.add(new WxMaSubscribeMessage.MsgData("date4", dfDateTime.format(order.getRefundTime())));
        sendSubscribe(openId , data , REFUND_TMPL_ID);
    }

    /**
     * 新订单审核订阅通知
     * @param openId 用户openID
     * @param order 订单信息
     */
    @Async
    public void newOrderSubscribe(String openId ,LitemallOrder order , LitemallOrderGoods orderGoods){
        // 模板数据 下面的数据，是需要根据申请的微信模板对应的，并不是每个人都一样
        List<WxMaSubscribeMessage.MsgData> data = new ArrayList<>();
        data.add(new WxMaSubscribeMessage.MsgData("character_string1", order.getOrderSn()));
        data.add(new WxMaSubscribeMessage.MsgData("phrase2", OrderStatus.orderStatusText(order)));
        data.add(new WxMaSubscribeMessage.MsgData("thing4", orderGoods.getGoodsName()));
        data.add(new WxMaSubscribeMessage.MsgData("thing10", order.getAddress()));
        data.add(new WxMaSubscribeMessage.MsgData("thing8", "请12小时内及时发货"));
        sendSubscribe(openId , data , NEW_ORDER_TMPL_ID);
    }

    /**
     * 新消息订阅通知
     * @param openId 用户openID
     * @param message 消息内容
     */
    @Async
    public void newMessageSubscribe(String openId , LitemallMessage message){
        // 模板数据 下面的数据，是需要根据申请的微信模板对应的，并不是每个人都一样
        List<WxMaSubscribeMessage.MsgData> data = new ArrayList<>();
        data.add(new WxMaSubscribeMessage.MsgData("thing1", message.getNickName()));
        data.add(new WxMaSubscribeMessage.MsgData("thing2", message.getContent()));
        data.add(new WxMaSubscribeMessage.MsgData("time3", dfDateTime.format(message.getAddTime())));
        data.add(new WxMaSubscribeMessage.MsgData("thing4", "未读消息"));
        data.add(new WxMaSubscribeMessage.MsgData("thing5", "您有新消息，记得回复哦！"));
        sendSubscribe(openId , data , NEW_MESSAGE_TMPL_ID);
    }

    /**
     * 发送订阅消息
     * @param openId 用户openId
     * @param data   模板信息
     * @param templateId 模板id
     */
    private void sendSubscribe(String openId, List<WxMaSubscribeMessage.MsgData> data, String templateId){
        WxMaMsgService wxMaMsgService = wxMaService.getMsgService();
        // 3.8.0版本使用的使用WxMaSubscribeMessage
        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                //这里添加的是推送消息的目标对象openId
                .toUser(openId)
                //这里填写的就是在后台申请添加的模板ID
                .templateId(templateId)
                //添加请求参数
                .data(data)
                //添加跳转链接，如果目标用户点击了推送的消息，则会跳转到小程序主页
                .page("/pages/index/index")
                .build();

        try {
            //推送消息
            wxMaMsgService.sendSubscribeMsg(subscribeMessage);
        } catch (WxErrorException e) {
            log.info("消息订阅错误信息-------退款订阅-----错误信息：" + e.getMessage());
        }
    }

}
