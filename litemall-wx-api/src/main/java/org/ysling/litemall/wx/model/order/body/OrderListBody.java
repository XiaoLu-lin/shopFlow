package org.ysling.litemall.wx.model.order.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderListBody extends PageBody implements Serializable {

    /**
     * 查看类型
     */
    private Integer showType;


}
