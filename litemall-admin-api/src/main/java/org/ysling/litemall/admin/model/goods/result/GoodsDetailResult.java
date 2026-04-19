package org.ysling.litemall.admin.model.goods.result;

import lombok.Data;
import org.ysling.litemall.db.domain.*;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情
 * @author Ysling
 */
@Data
public class GoodsDetailResult implements Serializable {

    /**
     * 分类ID列表
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
     * 货品信息
     */
    private List<LitemallGoodsProduct> products;
    /**
     * 商品参数列表
     */
    private List<LitemallGoodsAttribute> attributes;
    /**
     * 商品规格列表
     */
    private List<LitemallGoodsSpecification> specifications;


}
