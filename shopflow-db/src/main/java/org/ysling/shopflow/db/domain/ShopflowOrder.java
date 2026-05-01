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
 * 订单表
 * </p>
 *
 * @author ysling
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("shopflow_order")
public class ShopflowOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 订单表ID
     */
    @TableId("`id`")
    private String id;
    /**
     * 用户表的用户ID
     */
    @TableField("`user_id`")
    private String userId;
    /**
     * 商品表的商品ID
     */
    @TableField("`goods_id`")
    private String goodsId;
    /**
     * 订单编号
     */
    @TableField("`order_sn`")
    private String orderSn;
    /**
     * 商户订单号
     */
    @TableField("`out_trade_no`")
    private String outTradeNo;
    /**
     * 店铺ID
     */
    @TableField("`brand_id`")
    private String brandId;
    /**
     * 订单状态
     */
    @TableField("`order_status`")
    private Short orderStatus;
    /**
     * 售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消
     */
    @TableField("`aftersale_status`")
    private Short aftersaleStatus;
    /**
     * 收货人名称
     */
    @TableField("`consignee`")
    private String consignee;
    /**
     * 收货人手机号
     */
    @TableField("`mobile`")
    private String mobile;
    /**
     * 收货具体地址
     */
    @TableField("`address`")
    private String address;
    /**
     * 用户订单留言
     */
    @TableField("`message`")
    private String message;
    /**
     * 商品总费用
     */
    @TableField("`goods_price`")
    private BigDecimal goodsPrice;
    /**
     * 配送费用
     */
    @TableField("`freight_price`")
    private BigDecimal freightPrice;
    /**
     * 优惠券减免
     */
    @TableField("`coupon_price`")
    private BigDecimal couponPrice;
    /**
     * 用户积分减免
     */
    @TableField("`integral_price`")
    private BigDecimal integralPrice;
    /**
     * 团购优惠价减免
     */
    @TableField("`groupon_price`")
    private BigDecimal grouponPrice;
    /**
     * 订单提交时总费用
     */
    @TableField("`order_price`")
    private BigDecimal orderPrice;
    /**
     * 实付费用， = goods_price + freight_price - grouponPrice - integral_price - coupon_price
     */
    @TableField("`actual_price`")
    private BigDecimal actualPrice;
    /**
     * 微信付款编号
     */
    @TableField("`pay_id`")
    private String payId;
    /**
     * 微信付款时间
     */
    @TableField("`pay_time`")
    private LocalDateTime payTime;
    /**
     * 发货编号
     */
    @TableField("`ship_sn`")
    private String shipSn;
    /**
     * 发货快递公司
     */
    @TableField("`ship_channel`")
    private String shipChannel;
    /**
     * 发货开始时间
     */
    @TableField("`ship_time`")
    private LocalDateTime shipTime;
    /**
     * 实际退款金额，（有可能退款金额小于实际支付金额）
     */
    @TableField("`refund_amount`")
    private BigDecimal refundAmount;
    /**
     * 退款方式
     */
    @TableField("`refund_type`")
    private String refundType;
    /**
     * 退款备注
     */
    @TableField("`refund_content`")
    private String refundContent;
    /**
     * 退款时间
     */
    @TableField("`refund_time`")
    private LocalDateTime refundTime;
    /**
     * 用户确认收货时间
     */
    @TableField("`confirm_time`")
    private LocalDateTime confirmTime;
    /**
     * 待评价订单商品数量
     */
    @TableField("`comments`")
    private Short comments;
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
     * 订单表ID
     */
    public static final String ID = "`id`";
    /**
     * 用户表的用户ID
     */
    public static final String USER_ID = "`user_id`";
    /**
     * 商品表的商品ID
     */
    public static final String GOODS_ID = "`goods_id`";
    /**
     * 订单编号
     */
    public static final String ORDER_SN = "`order_sn`";
    /**
     * 商户订单号
     */
    public static final String OUT_TRADE_NO = "`out_trade_no`";
    /**
     * 店铺ID
     */
    public static final String BRAND_ID = "`brand_id`";
    /**
     * 订单状态
     */
    public static final String ORDER_STATUS = "`order_status`";
    /**
     * 售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消
     */
    public static final String AFTERSALE_STATUS = "`aftersale_status`";
    /**
     * 收货人名称
     */
    public static final String CONSIGNEE = "`consignee`";
    /**
     * 收货人手机号
     */
    public static final String MOBILE = "`mobile`";
    /**
     * 收货具体地址
     */
    public static final String ADDRESS = "`address`";
    /**
     * 用户订单留言
     */
    public static final String MESSAGE = "`message`";
    /**
     * 商品总费用
     */
    public static final String GOODS_PRICE = "`goods_price`";
    /**
     * 配送费用
     */
    public static final String FREIGHT_PRICE = "`freight_price`";
    /**
     * 优惠券减免
     */
    public static final String COUPON_PRICE = "`coupon_price`";
    /**
     * 用户积分减免
     */
    public static final String INTEGRAL_PRICE = "`integral_price`";
    /**
     * 团购优惠价减免
     */
    public static final String GROUPON_PRICE = "`groupon_price`";
    /**
     * 订单提交时总费用
     */
    public static final String ORDER_PRICE = "`order_price`";
    /**
     * 实付费用， = goods_price + freight_price - grouponPrice - integral_price - coupon_price
     */
    public static final String ACTUAL_PRICE = "`actual_price`";
    /**
     * 微信付款编号
     */
    public static final String PAY_ID = "`pay_id`";
    /**
     * 微信付款时间
     */
    public static final String PAY_TIME = "`pay_time`";
    /**
     * 发货编号
     */
    public static final String SHIP_SN = "`ship_sn`";
    /**
     * 发货快递公司
     */
    public static final String SHIP_CHANNEL = "`ship_channel`";
    /**
     * 发货开始时间
     */
    public static final String SHIP_TIME = "`ship_time`";
    /**
     * 实际退款金额，（有可能退款金额小于实际支付金额）
     */
    public static final String REFUND_AMOUNT = "`refund_amount`";
    /**
     * 退款方式
     */
    public static final String REFUND_TYPE = "`refund_type`";
    /**
     * 退款备注
     */
    public static final String REFUND_CONTENT = "`refund_content`";
    /**
     * 退款时间
     */
    public static final String REFUND_TIME = "`refund_time`";
    /**
     * 用户确认收货时间
     */
    public static final String CONFIRM_TIME = "`confirm_time`";
    /**
     * 待评价订单商品数量
     */
    public static final String COMMENTS = "`comments`";
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
