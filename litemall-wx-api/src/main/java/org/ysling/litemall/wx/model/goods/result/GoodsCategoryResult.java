package org.ysling.litemall.wx.model.goods.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallCategory;

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
    private LitemallCategory currentCategory;

    /**
     * 父分类
     */
    private LitemallCategory parentCategory;

    /**
     * 子分类列表
     */
    private List<LitemallCategory> brotherCategory;


}
