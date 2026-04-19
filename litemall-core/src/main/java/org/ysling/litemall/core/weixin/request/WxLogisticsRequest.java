package org.ysling.litemall.core.weixin.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 微信物流查询插件请求信息
 * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/industry/express/business/express_search.html
 * @author Ysling
 */
@Data
public class WxLogisticsRequest {

    /**是否必填：是    用户openid*/
    @JsonProperty("openid")
    private String openid;

    /**是否必填：否   寄件人手机号*/
    @JsonProperty("sender_phone")
    private String senderPhone;

    /**是否必填：是   收件人手机号，部分运力需要用户手机号作为查单依据*/
    @JsonProperty("receiver_phone")
    private String receiverPhone;

    /**是否必填：否   运力id（运单号所属运力公司id）*/
    @JsonProperty("delivery_id")
    private String deliveryId;

    /**是否必填：是   运单号*/
    @JsonProperty("waybill_id")
    private String waybillId;

    /**是否必填：是   交易单号（微信支付生成的交易单号，一般以420开头）*/
    @JsonProperty("trans_id")
    private String transId;

    /**是否必填：否   点击落地页商品卡片跳转路径（建议为订单详情页path），不传默认跳转小程序首页。*/
    @JsonProperty("order_detail_path")
    private String orderDetailPath;

    /**是否必填：是   商品信息  map：key == detail_list */
    @JsonProperty("goodsI_info")
    private Map<String, List<GoodsDetail>> goodsInfo;

}
