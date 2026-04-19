package org.ysling.litemall.admin.model.topic.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.domain.LitemallTopic;

import java.io.Serializable;
import java.util.List;

/**
 * 专题详情
 * @author Ysling
 */
@Data
public class TopicReadResult implements Serializable {

    /**
     * 专题信息
     */
    private LitemallTopic topic;

    /**
     * 商品列表
     */
    private List<LitemallGoods> goodsList;

}
