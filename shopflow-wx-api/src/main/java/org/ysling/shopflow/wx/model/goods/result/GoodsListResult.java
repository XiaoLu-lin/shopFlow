package org.ysling.shopflow.wx.model.goods.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowCategory;
import org.ysling.shopflow.db.entity.PageResult;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class GoodsListResult implements Serializable {

    /**
     * 数据列表
     */
    private List<?> list;

    /**
     * 总数据条数
     */
    private long total;

    /**
     * 当前页码
     */
    private long page;

    /**
     * 每页数量
     */
    private long limit;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 分类列表
     */
    private List<ShopflowCategory> filterCategoryList;

}
