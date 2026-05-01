package org.ysling.shopflow.wx.model.topic.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TopicListBody extends PageBody implements Serializable {

    /**
     * 专题标题
     */
    private String title;

}
