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
 * webSocket消息
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_message")
public class LitemallMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * socket消息ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 发送方头像图片
     */
    @TableField("`avatar_url`")
    private String avatarUrl;
    /**
     * 发送方昵称
     */
    @TableField("`nick_name`")
    private String nickName;
    /**
     * 发送方用户ID
     */
    @TableField("`send_user_id`")
    private String sendUserId;
    /**
     * 接收方用户ID
     */
    @TableField("`receive_user_id`")
    private String receiveUserId;
    /**
     * 消息内容
     */
    @TableField("`content`")
    private String content;
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
     * socket消息ID
     */
    public static final String ID = "`id`";
    /**
     * 发送方头像图片
     */
    public static final String AVATAR_URL = "`avatar_url`";
    /**
     * 发送方昵称
     */
    public static final String NICK_NAME = "`nick_name`";
    /**
     * 发送方用户ID
     */
    public static final String SEND_USER_ID = "`send_user_id`";
    /**
     * 接收方用户ID
     */
    public static final String RECEIVE_USER_ID = "`receive_user_id`";
    /**
     * 消息内容
     */
    public static final String CONTENT = "`content`";
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
