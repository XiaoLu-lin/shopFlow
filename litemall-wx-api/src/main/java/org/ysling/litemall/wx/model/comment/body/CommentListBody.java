package org.ysling.litemall.wx.model.comment.body;

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
public class CommentListBody extends PageBody implements Serializable {

    /**
     * 类型ID
     */
    @NotValue(message = "类型ID不能为空")
    private String valueId;
    /**
     * 评论类型
     */
    @NotValue(message = "评论类型不能为空")
    private Short commentType;


}
