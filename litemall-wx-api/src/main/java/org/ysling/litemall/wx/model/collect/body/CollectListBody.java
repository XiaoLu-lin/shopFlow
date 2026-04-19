package org.ysling.litemall.wx.model.collect.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CollectListBody extends PageBody implements Serializable {

    /**
     * 类型，如果是0则是商品收藏，如果是1则是专题收藏
     */
    @NotValue(message = "类型不能为空")
    private Byte type;

}
