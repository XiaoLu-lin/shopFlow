package org.ysling.shopflow.wx.model.brand.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class BrandGoodsDetailResult implements Serializable {

    /**
     * 分类ids
     */
    private String[] categoryIds;

    /**
     * 商品信息
     */
    private ShopflowGoods goods;

    /**
     * 团购规则
     */
    private ShopflowGrouponRules grouponRules;

    /**
     * 商品货品信息
     */
    private List<ShopflowGoodsProduct> products;

    /**
     * 商品参数信息
     */
    private List<ShopflowGoodsAttribute> attributes;

    /**
     * 商品规格信息
     */
    private List<ShopflowGoodsSpecification> specifications;



}
