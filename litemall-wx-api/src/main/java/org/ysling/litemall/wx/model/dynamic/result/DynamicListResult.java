package org.ysling.litemall.wx.model.dynamic.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 动态列表返回结果
 * @author Ysling
 */
@Data
public class DynamicListResult implements Serializable {

    /**
     * 动态表ID
     */
    private String id;
    /**
     * 用户表的用户ID
     */
    private String userId;
    /**
     * 发表内容
     */
    private String content;
    /**
     * 图片地址列表，采用JSON数组格式
     */
    private String[] picUrls;
    /**
     * 点赞量
     */
    private Long likeCount;
    /**
     * 访问量
     */
    private Long lookCount;
    /**
     * 评论数量
     */
    private Long commentCount;
    /**
     * 是否管理员，0不是，1是
     */
    private Boolean isAdmin;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 添加日期
     */
    private String addTime;
    /**
     * 是否点赞
     */
    private Boolean dynamicLike;

}
