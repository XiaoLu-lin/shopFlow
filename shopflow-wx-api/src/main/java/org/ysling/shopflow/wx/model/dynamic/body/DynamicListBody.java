package org.ysling.shopflow.wx.model.dynamic.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

/**
 * 动态列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicListBody extends PageBody {

    /**
     * 动态内容
     */
    private String content;


}
