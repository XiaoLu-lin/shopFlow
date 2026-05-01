package org.ysling.shopflow.wx.model.goods.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowCategory;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class GoodsCategoryResult implements Serializable {

    /**
     * 当前分类
     */
    private ShopflowCategory currentCategory;

    /**
     * 父分类
     */
    private ShopflowCategory parentCategory;

    /**
     * 子分类列表
     */
    private List<ShopflowCategory> brotherCategory;


}
