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
 * 优惠券信息及规则表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_coupon")
public class ShopflowCoupon implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 优惠券规则表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 优惠券名称
     */
    @TableField("`name`")
    private String name;
    /**
     * 优惠券介绍，通常是显示优惠券使用限制文字
     */
    @TableField("`depict`")
    private String depict;
    /**
     * 优惠券标签，例如新人专用
     */
    @TableField("`tag`")
    private String tag;
    /**
     * 优惠券数量，如果是0，则是无限量
     */
    @TableField("`total`")
    private Integer total;
    /**
     * 优惠金额，
     */
    @TableField("`discount`")
    private BigDecimal discount;
    /**
     * 最少消费金额才能使用优惠券。
     */
    @TableField("`min`")
    private BigDecimal min;
    /**
     * 用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.
     */
    @TableField("`limit`")
    private Short limit;
    /**
     * 优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；
     */
    @TableField("`type`")
    private Short type;
    /**
     * 优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。
     */
    @TableField("`status`")
    private Short status;
    /**
     * 商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。
     */
    @TableField("`goods_type`")
    private Short goodsType;
    /**
     * 商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。
     */
    @TableField(value = "`goods_ids`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] goodsIds;
    /**
     * 优惠券兑换码
     */
    @TableField("`code`")
    private String code;
    /**
     * 有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；
     */
    @TableField("`time_type`")
    private Short timeType;
    /**
     * 基于领取时间的有效天数days。
     */
    @TableField("`days`")
    private Short days;
    /**
     * 使用券开始时间
     */
    @TableField("`start_time`")
    private LocalDateTime startTime;
    /**
     * 使用券截至时间
     */
    @TableField("`end_time`")
    private LocalDateTime endTime;
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
     * 优惠券规则表ID
     */
    public static final String ID = "`id`";
    /**
     * 优惠券名称
     */
    public static final String NAME = "`name`";
    /**
     * 优惠券介绍，通常是显示优惠券使用限制文字
     */
    public static final String DEPICT = "`depict`";
    /**
     * 优惠券标签，例如新人专用
     */
    public static final String TAG = "`tag`";
    /**
     * 优惠券数量，如果是0，则是无限量
     */
    public static final String TOTAL = "`total`";
    /**
     * 优惠金额，
     */
    public static final String DISCOUNT = "`discount`";
    /**
     * 最少消费金额才能使用优惠券。
     */
    public static final String MIN = "`min`";
    /**
     * 用户领券限制数量，如果是0，则是不限制；默认是1，限领一张.
     */
    public static final String LIMIT = "`limit`";
    /**
     * 优惠券赠送类型，如果是0则通用券，用户领取；如果是1，则是注册赠券；如果是2，则是优惠券码兑换；
     */
    public static final String TYPE = "`type`";
    /**
     * 优惠券状态，如果是0则是正常可用；如果是1则是过期; 如果是2则是下架。
     */
    public static final String STATUS = "`status`";
    /**
     * 商品限制类型，如果0则全商品，如果是1则是类目限制，如果是2则是商品限制。
     */
    public static final String GOODS_TYPE = "`goods_type`";
    /**
     * 商品限制值，goods_type如果是0则空集合，如果是1则是类目集合，如果是2则是商品集合。
     */
    public static final String GOODS_IDS = "`goods_ids`";
    /**
     * 优惠券兑换码
     */
    public static final String CODE = "`code`";
    /**
     * 有效时间限制，如果是0，则基于领取时间的有效天数days；如果是1，则start_time和end_time是优惠券有效期；
     */
    public static final String TIME_TYPE = "`time_type`";
    /**
     * 基于领取时间的有效天数days。
     */
    public static final String DAYS = "`days`";
    /**
     * 使用券开始时间
     */
    public static final String START_TIME = "`start_time`";
    /**
     * 使用券截至时间
     */
    public static final String END_TIME = "`end_time`";
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
