package org.ysling.litemall.admin.model.order.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallOrderGoods;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情列表
 * @author Ysling
 */
@Data
public class OrderListResult implements Serializable {

    /**
     * 订单表ID
     */
    private String id;

    /**
     * 用户表的用户ID
     */
    private String userId;

    /**
     * 待评价订单商品数量
     */
    private Short comments;

    /**
     * 用户昵称或网络名称
     */
    private String userName;

    /**
     * 用户手机号码
     */
    private String userMobile;

    /**
     * 用户头像图片
     */
    private String userAvatar;

    /**
     * 商品数量
     */
    private Integer goodsNumber;

    /**
     * 商品表的商品ID
     */
    private String goodsId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 店铺ID
     */
    private String brandId;

    /**
     * 订单状态
     */
    private Short orderStatus;

    /**
     * 订单状态文本
     */
    private String orderStatusText;

    /**
     * 物流公司
     */
    private String shipChannel;

    /**
     * 售后状态，0是可申请，1是用户已申请，2是管理员审核通过，3是管理员退款成功，4是管理员审核拒绝，5是用户已取消
     */
    private Short aftersaleStatus;

    /**
     * 收货人名称
     */
    private String consignee;

    /**
     * 收货人手机号
     */
    private String mobile;

    /**
     * 收货具体地址
     */
    private String address;

    /**
     * 用户订单留言
     */
    private String message;

    /**
     * 商品总费用
     */
    private BigDecimal goodsPrice;

    /**
     * 配送费用
     */
    private BigDecimal freightPrice;

    /**
     * 优惠券减免
     */
    private BigDecimal couponPrice;

    /**
     * 用户余额减免
     */
    private BigDecimal integralPrice;

    /**
     * 团购优惠价减免
     */
    private BigDecimal grouponPrice;

    /**
     * 订单提交时总费用
     */
    private BigDecimal orderPrice;

    /**
     * 实付费用， = goods_price + freight_price - grouponPrice - integral_price - coupon_price
     */
    private BigDecimal actualPrice;

    /**
     * 微信付款编号
     */
    private String payId;

    /**
     * 微信付款时间
     */
    private LocalDateTime payTime;

    /**
     * 发货编号
     */
    private String shipSn;

    /**
     * 发货开始时间
     */
    private LocalDateTime shipTime;

    /**
     * 实际退款金额，（有可能退款金额小于实际支付金额）
     */
    private BigDecimal refundAmount;

    /**
     * 退款方式
     */
    private String refundType;

    /**
     * 退款备注
     */
    private String refundContent;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 用户确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 商品列表
     */
    private List<LitemallOrderGoods> goodsVoList;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
