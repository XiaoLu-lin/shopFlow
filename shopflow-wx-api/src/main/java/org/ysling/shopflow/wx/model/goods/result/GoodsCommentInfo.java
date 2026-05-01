package org.ysling.shopflow.wx.model.goods.result;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class GoodsCommentInfo implements Serializable {

    /**
     * 评论表ID
     */
    private String id;

    /**
     * 评分， 1-5
     */
    private Short star;

    /**
     * 用户表的用户ID
     */
    private String userId;

    /**
     * 则是商品id
     */
    private String goodsId;

    /**
     * 图片地址列表，采用JSON数组格式
     */
    private String[] picUrls;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 管理员回复内容
     */
    private String adminContent;

    /**
     * 是否含有图片
     */
    private Boolean hasPicture;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

}
