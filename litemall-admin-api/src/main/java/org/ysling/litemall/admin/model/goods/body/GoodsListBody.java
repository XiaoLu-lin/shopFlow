package org.ysling.litemall.admin.model.goods.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 商品列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsListBody extends PageBody {

    /**
     * 商品名称
     */
    private String name;
    /**
     * 审核状态
     */
    private Short status;
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 店铺ID
     */
    private String brandId;
    /**
     * 商品编号
     */
    private String goodsSn;



}
