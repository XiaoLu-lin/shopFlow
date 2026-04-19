package org.ysling.litemall.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.litemall.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 评论表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_comment")
public class LitemallComment implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 评论表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户表的用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 动态ID，专题ID，店铺ID
     */
    @TableField("`value_id`")
    private String valueId;
    /**
     * 0：动态评论，1：专题评论，2：店铺评论
     */
    @TableField("`type`")
    private Short type;
    /**
     * 评论内容
     */
    @TableField("`content`")
    private String content;
    /**
     * 图片地址列表，采用JSON数组格式
     */
    @TableField(value = "`pic_urls`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] picUrls;
    /**
     * 点赞量
     */
    @TableField("`like_count`")
    private Long likeCount;
    /**
     * 被回复的评论ID
     */
    @TableField("`reply_id`")
    private String replyId;
    /**
     * 被回复的用户ID
     */
    @TableField("`reply_user_id`")
    private String replyUserId;
    /**
     * 创建时间
     */
    @TableField(value = "`add_time`", fill = FieldFill.INSERT)
    private LocalDateTime addTime;
    /**
     * 更新时间
     */
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删除
     */
    @TableField("`deleted`")
    @TableLogic
    private Boolean deleted;
    /**
     * 租户ID，用于分割多个租户
     */
    @TableField("`tenant_id`")
    private String tenantId;
    /**
     * 更新版本号
     */
    @TableField("`version`")
    @Version
    private Integer version;

    /////////////////////////////////
    // 数据库字段常量
    ////////////////////////////////

    /**
     * 评论表ID
     */
    public static final String ID = "`id`";
    /**
     * 用户表的用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 动态ID，专题ID，店铺ID
     */
    public static final String VALUE_ID = "`value_id`";
    /**
     * 0：动态评论，1：专题评论，2：店铺评论
     */
    public static final String TYPE = "`type`";
    /**
     * 评论内容
     */
    public static final String CONTENT = "`content`";
    /**
     * 图片地址列表，采用JSON数组格式
     */
    public static final String PIC_URLS = "`pic_urls`";
    /**
     * 点赞量
     */
    public static final String LIKE_COUNT = "`like_count`";
    /**
     * 被回复的评论ID
     */
    public static final String REPLY_ID = "`reply_id`";
    /**
     * 被回复的用户ID
     */
    public static final String REPLY_USER_ID = "`reply_user_id`";
    /**
     * 创建时间
     */
    public static final String ADD_TIME = "`add_time`";
    /**
     * 更新时间
     */
    public static final String UPDATE_TIME = "`update_time`";
    /**
     * 逻辑删除
     */
    public static final String DELETED = "`deleted`";
    /**
     * 租户ID，用于分割多个租户
     */
    public static final String TENANT_ID = "`tenant_id`";
    /**
     * 更新版本号
     */
    public static final String VERSION = "`version`";
}
