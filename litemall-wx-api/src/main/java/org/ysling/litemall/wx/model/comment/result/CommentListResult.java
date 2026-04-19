package org.ysling.litemall.wx.model.comment.result;

import lombok.Data;
import org.ysling.litemall.db.entity.UserInfo;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class CommentListResult implements Serializable {

    /**
     * 评论表ID
     */
    private String commentId;

    /**
     * 用户表的用户ID
     */
    private String userId;

    /**
     * 动态ID，专题ID，店铺ID
     */
    private String valueId;

    /**
     * 0：动态评论，1：专题评论，2：店铺评论
     */
    private Short commentType;

    /**
     * 是否点赞
     */
    private Boolean commentLike;
    /**
     * 是否展开回复列表
     */
    private Boolean showReply;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 图片地址列表，采用JSON数组格式
     */
    private String[] picList;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 被回复的评论ID
     */
    private String replyId;

    /**
     * 被回复的用户ID
     */
    private String replyUserId;

    /**
     * 回复数量
     */
    private Integer replyCount;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
     * 回复人信息
     */
    private UserInfo replyUserInfo;

    /**
     * 回复列表
     */
    private Object replyList;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;


}
