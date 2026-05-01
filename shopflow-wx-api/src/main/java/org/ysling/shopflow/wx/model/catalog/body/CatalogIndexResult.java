package org.ysling.shopflow.wx.model.catalog.body;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowCategory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Ysling
 */
@Data
public class CatalogIndexResult implements Serializable {

    /**
     * 当前一级分类目录
     */
    private ShopflowCategory currentCategory;

    /**
     * 所有一级分类目录
     */
    private List<ShopflowCategory> categoryList;

    /**
     * 子分类列表
     */
    private List<ShopflowCategory> currentSubCategory;

    /**
     * 所有子分类列表
     */
    private Map<String, List<ShopflowCategory>> allList;


}
