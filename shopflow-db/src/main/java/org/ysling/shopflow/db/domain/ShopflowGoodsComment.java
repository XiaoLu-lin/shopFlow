package org.ysling.shopflow.db.domain;

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
import org.ysling.shopflow.db.handler.*;
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
@TableName("shopflow_goods_comment")
public class ShopflowGoodsComment implements Serializable {

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
     * 则是商品id
     */
    @TableField("`goods_id`")
    private String goodsId;
    /**
     * 评分， 1-5
     */
    @TableField("`star`")
    private Short star;
    /**
     * 图片地址列表，采用JSON数组格式
     */
    @TableField(value = "`pic_urls`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] picUrls;
    /**
     * 评论内容
     */
    @TableField("`content`")
    private String content;
    /**
     * 管理员回复内容
     */
    @TableField("`admin_content`")
    private String adminContent;
    /**
     * 是否含有图片
     */
    @TableField("`has_picture`")
    private Boolean hasPicture;
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
     * 则是商品id
     */
    public static final String GOODS_ID = "`goods_id`";
    /**
     * 评分， 1-5
     */
    public static final String STAR = "`star`";
    /**
     * 图片地址列表，采用JSON数组格式
     */
    public static final String PIC_URLS = "`pic_urls`";
    /**
     * 评论内容
     */
    public static final String CONTENT = "`content`";
    /**
     * 管理员回复内容
     */
    public static final String ADMIN_CONTENT = "`admin_content`";
    /**
     * 是否含有图片
     */
    public static final String HAS_PICTURE = "`has_picture`";
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
