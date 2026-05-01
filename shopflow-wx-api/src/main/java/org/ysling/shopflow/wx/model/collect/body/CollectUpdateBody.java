package org.ysling.shopflow.wx.model.collect.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class CollectUpdateBody implements Serializable {

    /**
     * 收藏类型
     */
    @NotValue(message = "收藏类型不能为空")
    private Byte type;
    /**
     * 收藏类型ID
     */
    @NotValue(message = "收藏类型ID不能为空")
    private String valueId;

}
