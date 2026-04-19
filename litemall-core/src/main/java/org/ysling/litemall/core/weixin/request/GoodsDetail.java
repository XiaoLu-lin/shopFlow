package org.ysling.litemall.core.weixin.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信物流查询商品详情
 * @author Ysling
 */
@Data
public class GoodsDetail {

    /**是否必填：是    商品名称*/
    @JsonProperty("goods_name")
    private String goodsName;

    /**是否必填：是    商品图片url*/
    @JsonProperty("goods_img_url")
    private String goodsImgUrl;

    /**是否必填：否    商品详情描述，不传默认取“商品名称”值，最多40汉字*/
    @JsonProperty("goods_desc")
    private String goodsDesc;

}
