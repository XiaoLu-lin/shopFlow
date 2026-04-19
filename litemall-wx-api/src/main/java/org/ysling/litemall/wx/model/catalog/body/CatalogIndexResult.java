package org.ysling.litemall.wx.model.catalog.body;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallCategory;

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
    private LitemallCategory currentCategory;

    /**
     * 所有一级分类目录
     */
    private List<LitemallCategory> categoryList;

    /**
     * 子分类列表
     */
    private List<LitemallCategory> currentSubCategory;

    /**
     * 所有子分类列表
     */
    private Map<String, List<LitemallCategory>> allList;


}
