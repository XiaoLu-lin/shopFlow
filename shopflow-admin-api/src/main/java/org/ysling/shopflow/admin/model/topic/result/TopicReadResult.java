package org.ysling.shopflow.admin.model.topic.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowTopic;

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
    private ShopflowTopic topic;

    /**
     * 商品列表
     */
    private List<ShopflowGoods> goodsList;

}
