package org.ysling.litemall.wx.model.goods.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.validator.sort.Sort;

import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsListBody extends PageBody implements Serializable {

    /**
     * 分类ID
     */
    private String categoryId;
    /**
     * 店铺ID
     */
    private String brandId;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 新品
     */
    private Boolean isNew;
    /**
     * 热卖
     */
    private Boolean isHot;
    /**
     * 管理员
     */
    private Boolean isAdmin;

    /**
     * 排序字段
     */
    @Sort(accepts = {"add_time", "retail_price", "name"})
    private String sort = "add_time";

}
