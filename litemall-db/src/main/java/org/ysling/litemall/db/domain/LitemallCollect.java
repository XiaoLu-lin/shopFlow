package org.ysling.litemall.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ysling.litemall.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 收藏表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_collect")
public class LitemallCollect implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 收藏表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户表的用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 如果type=0，则是商品ID；如果type=1，则是专题ID
     */
    @TableField("`value_id`")
    private String valueId;
    /**
     * 收藏名称
     */
    @TableField("`name`")
    private String name;
    /**
     * 相关商品最低价
     */
    @TableField("`price`")
    private BigDecimal price;
    /**
     * 收藏简介
     */
    @TableField("`brief`")
    private String brief;
    /**
     * 收藏图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID
     */
    @TableField("`type`")
    private Byte type;
    /**
     * 是否取消收藏
     */
    @TableField("`cancel`")
    private Boolean cancel;
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
     * 收藏表ID
     */
    public static final String ID = "`id`";
    /**
     * 用户表的用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 如果type=0，则是商品ID；如果type=1，则是专题ID
     */
    public static final String VALUE_ID = "`value_id`";
    /**
     * 收藏名称
     */
    public static final String NAME = "`name`";
    /**
     * 相关商品最低价
     */
    public static final String PRICE = "`price`";
    /**
     * 收藏简介
     */
    public static final String BRIEF = "`brief`";
    /**
     * 收藏图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 收藏类型，如果type=0，则是商品ID；如果type=1，则是专题ID
     */
    public static final String TYPE = "`type`";
    /**
     * 是否取消收藏
     */
    public static final String CANCEL = "`cancel`";
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
