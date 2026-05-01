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
 * 订单商品表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_order_goods")
public class ShopflowOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 订单商品表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 订单表的订单ID
     */
    @TableField("`order_id`")
    private String orderId;
    /**
     * 商品表的商品ID
     */
    @TableField("`goods_id`")
    private String goodsId;
    /**
     * 商品名称
     */
    @TableField("`goods_name`")
    private String goodsName;
    /**
     * 商品编号
     */
    @TableField("`goods_sn`")
    private String goodsSn;
    /**
     * 商品货品表的货品ID
     */
    @TableField("`product_id`")
    private String productId;
    /**
     * 商品货品的购买数量
     */
    @TableField("`number`")
    private Short number;
    /**
     * 商品货品的售价
     */
    @TableField("`price`")
    private BigDecimal price;
    /**
     * 自提地址
     */
    @TableField("`address`")
    private String address;
    /**
     * 是否自提
     */
    @TableField("`is_take_their`")
    private Boolean isTakeTheir;
    /**
     * 商品货品的规格列表
     */
    @TableField(value = "`specifications`", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] specifications;
    /**
     * 商品货品图片或者商品图片
     */
    @TableField("`pic_url`")
    private String picUrl;
    /**
     * 订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果是1，则追加评价；如果其他值，则是comment表里面的评论ID。
     */
    @TableField("`comment`")
    private Integer comment;
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
     * 订单商品表ID
     */
    public static final String ID = "`id`";
    /**
     * 订单表的订单ID
     */
    public static final String ORDER_ID = "`order_id`";
    /**
     * 商品表的商品ID
     */
    public static final String GOODS_ID = "`goods_id`";
    /**
     * 商品名称
     */
    public static final String GOODS_NAME = "`goods_name`";
    /**
     * 商品编号
     */
    public static final String GOODS_SN = "`goods_sn`";
    /**
     * 商品货品表的货品ID
     */
    public static final String PRODUCT_ID = "`product_id`";
    /**
     * 商品货品的购买数量
     */
    public static final String NUMBER = "`number`";
    /**
     * 商品货品的售价
     */
    public static final String PRICE = "`price`";
    /**
     * 自提地址
     */
    public static final String ADDRESS = "`address`";
    /**
     * 是否自提
     */
    public static final String IS_TAKE_THEIR = "`is_take_their`";
    /**
     * 商品货品的规格列表
     */
    public static final String SPECIFICATIONS = "`specifications`";
    /**
     * 商品货品图片或者商品图片
     */
    public static final String PIC_URL = "`pic_url`";
    /**
     * 订单商品评论，如果是-1，则超期不能评价；如果是0，则可以评价；如果是1，则追加评价；如果其他值，则是comment表里面的评论ID。
     */
    public static final String COMMENT = "`comment`";
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
