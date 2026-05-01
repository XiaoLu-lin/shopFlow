package org.ysling.shopflow.admin.model.order.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;

/**
 * 回复订单商品
 * @author Ysling
 */
@Data
public class OrderReplyBody implements Serializable {

    /**
     * 评论ID
     */
    @NotValue(message = "评论ID不能为空")
    private String commentId;
    /**
     * 回复内容
     */
    @NotValue(message = "回复内容不能为空")
    private String content;

}
