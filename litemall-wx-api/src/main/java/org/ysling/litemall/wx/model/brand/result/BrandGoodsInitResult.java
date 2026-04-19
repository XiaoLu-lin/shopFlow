package org.ysling.litemall.wx.model.brand.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Data
public class BrandGoodsInitResult implements Serializable {

    /**
     * 订单服务费比例 %
     */
    private BigDecimal brokerage;
    /**
     * 商品可设置的最小金额 单位元
     */
    private Integer minAmount;
    /**
     * 商品可设置的最大金额 单位元
     */
    private Integer maxAmount;


}
