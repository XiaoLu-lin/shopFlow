package org.ysling.shopflow.wx.model.order.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

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
