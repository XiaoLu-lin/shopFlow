package org.ysling.shopflow.wx.model.goods.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsCommentListBody extends PageBody implements Serializable {

    /**
     * 商品ID
     */
    @NotValue(message = "商品ID不能为空")
    private String goodsId;
    /**
     * 是否只查看有图
     */
    @NotValue(message = "是否只查看有图不能为空")
    private Boolean hasPicture;

    public GoodsCommentListBody(String goodsId , Integer limit){
        this.goodsId = goodsId;
        super.setLimit(limit);
    }

}
