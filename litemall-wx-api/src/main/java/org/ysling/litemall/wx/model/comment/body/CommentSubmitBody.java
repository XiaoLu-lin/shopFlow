package org.ysling.litemall.wx.model.comment.body;

import lombok.Data;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class CommentSubmitBody implements Serializable {

    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论类型
     */
    private Short commentType;
    /**
     * 类型ID
     */
    private String valueId;
    /**
     * 评论ID
     */
    private String commentId;
    /**
     * 回复ID
     */
    private String replyUserId;
    /**
     * 评论图片
     */
    private String[] commentImage;

}
