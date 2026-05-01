package org.ysling.shopflow.wx.model.groupon.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowGroupon;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class GrouponJoinResult implements Serializable {

    /**
     * 商品ID
     */
    private String goodId;
    /**
     * 团购信息
     */
    private ShopflowGroupon groupon;

}
