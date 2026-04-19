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
 * 意见反馈表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_feedback")
public class LitemallFeedback implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 意见反馈表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户表的用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 用户名称
     */
    @TableField("`username`")
    private String username;
    /**
     * 手机号
     */
    @TableField("`mobile`")
    private String mobile;
    /**
     * 反馈类型
     */
    @TableField("`feed_type`")
    private String feedType;
    /**
     * 反馈内容
     */
    @TableField("`content`")
    private String content;
    /**
     * 状态
     */
    @TableField("`status`")
    private Boolean status;
    /**
     * 是否含有图片
     */
    @TableField("`has_picture`")
    private Boolean hasPicture;
    /**
     * 图片地址列表，采用JSON数组格式
     */
    @TableField(value = "`pic_urls`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] picUrls;
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
     * 意见反馈表ID
     */
    public static final String ID = "`id`";
    /**
     * 用户表的用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 用户名称
     */
    public static final String USERNAME = "`username`";
    /**
     * 手机号
     */
    public static final String MOBILE = "`mobile`";
    /**
     * 反馈类型
     */
    public static final String FEED_TYPE = "`feed_type`";
    /**
     * 反馈内容
     */
    public static final String CONTENT = "`content`";
    /**
     * 状态
     */
    public static final String STATUS = "`status`";
    /**
     * 是否含有图片
     */
    public static final String HAS_PICTURE = "`has_picture`";
    /**
     * 图片地址列表，采用JSON数组格式
     */
    public static final String PIC_URLS = "`pic_urls`";
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
