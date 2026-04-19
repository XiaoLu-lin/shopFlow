package org.ysling.litemall.wx.model.topic.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.domain.LitemallTopic;

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
    private LitemallTopic topic;

    /**
     * 商品列表
     */
    private List<LitemallGoods> goods;

    /**
     * 是否点赞
     */
    private Boolean topicLike;

    /**
     * 是否收藏
     */
    private Boolean userHasCollect;

}
