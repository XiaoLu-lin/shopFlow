package org.ysling.shopflow.wx.model.cart.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class CartCheckedBody implements Serializable {

    /**
     * 是否选中
     */
    @NotValue(message = "是否选中不能为空")
    private Integer isChecked;
    /**
     * 货品Ids
     */
    @NotValue(message = "货品Ids不能为空")
    private List<String> productIds;

}
