package org.ysling.litemall.admin.model.dynamic.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 动态列表返回结果
 * @author Ysling
 */
@Data
public class DynamicListResult implements Serializable {

    /**
     * 动态ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 发布内容
     */
    private String content;
    /**
     * 是否管理员
     */
    private String isAdmin;
    /**
     * 浏览次数
     */
    private Long lookCount;
    /**
     * 图片列表
     */
    private String[] picUrls;
    /**
     * 点赞次数
     */
    private Long likeCount;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 添加日期
     */
    private String addTime;

}
