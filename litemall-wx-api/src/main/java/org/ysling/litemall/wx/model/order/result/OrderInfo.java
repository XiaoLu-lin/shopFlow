package org.ysling.litemall.wx.model.order.result;

import lombok.Data;
import org.ysling.litemall.db.entity.OrderHandleOption;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class OrderInfo implements Serializable {

    /**
     * 订单表ID
     */
    private String id;

    /**
     * 用户表的用户ID
     */
    private String userId;

    /**
     * 商品表的商品ID
     */
    private String goodsId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 店铺ID
     */
    private String brandId;

    /**
     * 订单状态
     */
    private Short orderStatus;

    /**
     * 售后状态
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
     * 实付费用
     */
    private BigDecimal actualPrice;

    /**
     * 微信付款时间
     */
    private LocalDateTime payTime;

    /**
     * 发货快递公司编号
     */
    private String expCode;

    /**
     * 发货快递公司名称
     */
    private String expName;

    /**
     * 物流单号
     */
    private String expNo;

    /**
     * 物流是否查询成功
     */
    private Boolean expSuccess;

    /**
     * 订单状态描述
     */
    private String orderStatusText;

    /**
     * 用户可执行操作
     */
    private OrderHandleOption handleOption;
    /**
     * 下单时间
     */
    private LocalDateTime addTime;

}
