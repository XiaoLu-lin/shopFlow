package org.ysling.shopflow.wx.model.topic.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowTopic;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class TopicDetailResult implements Serializable {

    /**
     * 专题信息
     */
    private ShopflowTopic topic;

    /**
     * 商品列表
     */
    private List<ShopflowGoods> goods;

    /**
     * 是否点赞
     */
    private Boolean topicLike;

    /**
     * 是否收藏
     */
    private Boolean userHasCollect;

}
