package org.ysling.litemall.wx.model.goods.result;

import lombok.Data;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.db.entity.GoodsSpecificationVo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class GoodsDetailResult implements Serializable {

    /**
     * 商品信息
     */
    private LitemallGoods info;
    /**
     * 分享图片
     */
    private String shareImage;
    /**
     * 是否收藏
     */
    private Boolean userHasCollect;
    /**
     * 通用问题
     */
    private List<LitemallIssue> issue;
    /**
     * 商品评论
     */
    private GoodsCommentResult comment;
    /**
     * 商品规格信息
     */
    private List<GoodsSpecificationVo> specificationList;
    /**
     * 商品货品信息
     */
    private List<LitemallGoodsProduct> productList;
    /**
     * 商品参数
     */
    private List<LitemallGoodsAttribute> attribute;
    /**
     * 店铺信息
     */
    private LitemallBrand brand;
    /**
     * 团购信息
     */
    private List<LitemallGrouponRules> groupon;
    /**
     * 是否可以分享
     */
    private Boolean share;


}
