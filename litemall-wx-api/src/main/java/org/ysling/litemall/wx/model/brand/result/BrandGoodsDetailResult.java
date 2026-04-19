package org.ysling.litemall.wx.model.brand.result;

import lombok.Data;
import org.ysling.litemall.db.domain.*;

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
    private LitemallGoods goods;

    /**
     * 团购规则
     */
    private LitemallGrouponRules grouponRules;

    /**
     * 商品货品信息
     */
    private List<LitemallGoodsProduct> products;

    /**
     * 商品参数信息
     */
    private List<LitemallGoodsAttribute> attributes;

    /**
     * 商品规格信息
     */
    private List<LitemallGoodsSpecification> specifications;



}
