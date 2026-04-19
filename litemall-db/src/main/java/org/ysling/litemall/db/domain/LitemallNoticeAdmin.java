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
 * 通知管理员表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_notice_admin")
public class LitemallNoticeAdmin implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 通知管理员表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 接收通知的管理员ID
     */
    @TableField("`admin_id`")
    private String adminId;
    /**
     * 通知ID
     */
    @TableField("`notice_id`")
    private String noticeId;
    /**
     * 通知标题
     */
    @TableField("`notice_title`")
    private String noticeTitle;
    /**
     * 通知内容
     */
    @TableField("`notice_content`")
    private String noticeContent;
    /**
     * 阅读时间，如果是NULL则是未读状态
     */
    @TableField("`read_time`")
    private LocalDateTime readTime;
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
     * 通知管理员表ID
     */
    public static final String ID = "`id`";
    /**
     * 接收通知的管理员ID
     */
    public static final String ADMIN_ID = "`admin_id`";
    /**
     * 通知ID
     */
    public static final String NOTICE_ID = "`notice_id`";
    /**
     * 通知标题
     */
    public static final String NOTICE_TITLE = "`notice_title`";
    /**
     * 通知内容
     */
    public static final String NOTICE_CONTENT = "`notice_content`";
    /**
     * 阅读时间，如果是NULL则是未读状态
     */
    public static final String READ_TIME = "`read_time`";
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
