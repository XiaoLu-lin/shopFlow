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
 * 商品基本信息表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("litemall_goods")
public class LitemallGoods implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 商品基本信息表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 商品编号
     */
    @TableField("`goods_sn`")
    private String goodsSn;
    /**
     * 商品名称
     */
    @TableField("`name`")
    private String name;
    /**
     * 商品所属类目ID
     */
    @TableField("`category_id`")
    private String categoryId;
    /**
     * 品牌id
     */
    @TableField("`brand_id`")
    private String brandId;
    /**
     * 商品宣传图片列表，采用JSON数组格式
     */
    @TableField(value = "`gallery`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] gallery;
    /**
     * 商品关键字，采用逗号间隔
     */
    @TableField("`keywords`")
    private String keywords;
    /**
     * 商品简介
     */
    @TableField("`brief`")
    private String brief;
    /**
     * 商品页面商品图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 商品分享海报
     */
    @TableField("`share_url`")
    private String shareUrl;
    /**
     * 自提地址
     */
    @TableField("`address`")
    private String address;
    /**
     * 是否团购商品
     */
    @TableField("`is_groupon`")
    private Boolean isGroupon;
    /**
     * 是否新品首发，如果设置则可以在新品首发页面展示
     */
    @TableField("`is_new`")
    private Boolean isNew;
    /**
     * 是否人气推荐，如果设置则可以在人气推荐页面展示
     */
    @TableField("`is_hot`")
    private Boolean isHot;
    /**
     * 是否自提
     */
    @TableField("`is_take_their`")
    private Boolean isTakeTheir;
    /**
     * 商品单位，例如件、盒
     */
    @TableField("`unit`")
    private String unit;
    /**
     * 专柜价格
     */
    @TableField("`counter_price`")
    private BigDecimal counterPrice;
    /**
     * 零售价格
     */
    @TableField("`retail_price`")
    private BigDecimal retailPrice;
    /**
     * 商品详细介绍，是富文本格式
     */
    @TableField("`detail`")
    private String detail;
    /**
     * 商品备注
     */
    @TableField("`remarks`")
    private String remarks;
    /**
     * 商品状态：0待审核，1已上架，2已下架，3已驳回
     */
    @TableField("`status`")
    private Short status;
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
     * 商品基本信息表ID
     */
    public static final String ID = "`id`";
    /**
     * 商品编号
     */
    public static final String GOODS_SN = "`goods_sn`";
    /**
     * 商品名称
     */
    public static final String NAME = "`name`";
    /**
     * 商品所属类目ID
     */
    public static final String CATEGORY_ID = "`category_id`";
    /**
     * 品牌id
     */
    public static final String BRAND_ID = "`brand_id`";
    /**
     * 商品宣传图片列表，采用JSON数组格式
     */
    public static final String GALLERY = "`gallery`";
    /**
     * 商品关键字，采用逗号间隔
     */
    public static final String KEYWORDS = "`keywords`";
    /**
     * 商品简介
     */
    public static final String BRIEF = "`brief`";
    /**
     * 商品页面商品图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 商品分享海报
     */
    public static final String SHARE_URL = "`share_url`";
    /**
     * 自提地址
     */
    public static final String ADDRESS = "`address`";
    /**
     * 是否团购商品
     */
    public static final String IS_GROUPON = "`is_groupon`";
    /**
     * 是否新品首发，如果设置则可以在新品首发页面展示
     */
    public static final String IS_NEW = "`is_new`";
    /**
     * 是否人气推荐，如果设置则可以在人气推荐页面展示
     */
    public static final String IS_HOT = "`is_hot`";
    /**
     * 是否自提
     */
    public static final String IS_TAKE_THEIR = "`is_take_their`";
    /**
     * 商品单位，例如件、盒
     */
    public static final String UNIT = "`unit`";
    /**
     * 专柜价格
     */
    public static final String COUNTER_PRICE = "`counter_price`";
    /**
     * 零售价格
     */
    public static final String RETAIL_PRICE = "`retail_price`";
    /**
     * 商品详细介绍，是富文本格式
     */
    public static final String DETAIL = "`detail`";
    /**
     * 商品备注
     */
    public static final String REMARKS = "`remarks`";
    /**
     * 商品状态：0待审核，1已上架，2已下架，3已驳回
     */
    public static final String STATUS = "`status`";
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
