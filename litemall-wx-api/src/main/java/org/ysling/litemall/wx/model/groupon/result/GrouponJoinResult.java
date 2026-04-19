package org.ysling.litemall.wx.model.groupon.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallGroupon;

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
    private LitemallGroupon groupon;

}
