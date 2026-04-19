package org.ysling.litemall.wx.model.goods.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class GoodsCommentResult implements Serializable {

    /**
     * 评论数量
     */
    private int count;

    /**
     * 评论信息
     */
    private List<GoodsCommentInfo> data;

}
