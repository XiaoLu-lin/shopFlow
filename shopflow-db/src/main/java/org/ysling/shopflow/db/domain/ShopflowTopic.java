package org.ysling.shopflow.db.domain;

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
import org.ysling.shopflow.db.handler.*;
import java.util.Collection;
/**
 * <p>
 * 专题表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_topic")
public class ShopflowTopic implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 专题表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 专题标题
     */
    @TableField("`title`")
    private String title;
    /**
     * 专题子标题
     */
    @TableField("`subtitle`")
    private String subtitle;
    /**
     * 专题作者
     */
    @TableField("`author`")
    private String author;
    /**
     * 专题内容，富文本格式
     */
    @TableField("`content`")
    private String content;
    /**
     * 专题相关商品最低价
     */
    @TableField("`price`")
    private BigDecimal price;
    /**
     * 专题阅读量
     */
    @TableField("`read_count`")
    private String readCount;
    /**
     * 点赞量
     */
    @TableField("`like_count`")
    private Long likeCount;
    /**
     * 评论数量
     */
    @TableField("`comment_count`")
    private Long commentCount;
    /**
     * 专题图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 专题相关商品，采用JSON数组格式
     */
    @TableField(value = "`goods_ids`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] goodsIds;
    /**
     * 权重用于排序
     */
    @TableField("`weight`")
    private Integer weight;
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
     * 专题表ID
     */
    public static final String ID = "`id`";
    /**
     * 专题标题
     */
    public static final String TITLE = "`title`";
    /**
     * 专题子标题
     */
    public static final String SUBTITLE = "`subtitle`";
    /**
     * 专题作者
     */
    public static final String AUTHOR = "`author`";
    /**
     * 专题内容，富文本格式
     */
    public static final String CONTENT = "`content`";
    /**
     * 专题相关商品最低价
     */
    public static final String PRICE = "`price`";
    /**
     * 专题阅读量
     */
    public static final String READ_COUNT = "`read_count`";
    /**
     * 点赞量
     */
    public static final String LIKE_COUNT = "`like_count`";
    /**
     * 评论数量
     */
    public static final String COMMENT_COUNT = "`comment_count`";
    /**
     * 专题图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 专题相关商品，采用JSON数组格式
     */
    public static final String GOODS_IDS = "`goods_ids`";
    /**
     * 权重用于排序
     */
    public static final String WEIGHT = "`weight`";
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
