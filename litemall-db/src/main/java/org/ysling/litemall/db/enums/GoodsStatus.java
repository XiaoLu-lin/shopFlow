package org.ysling.litemall.db.enums;

import org.ysling.litemall.db.domain.LitemallGoods;

import java.io.Serializable;

/**
 * 商品审核状态
 * @author Ysling
 */
public enum GoodsStatus implements Serializable {

    /**商品待审核*/
    GOODS_AUDIT((short)0,"待审核"),
    /**商品已上架*/
    GOODS_ON_SALE((short)1,"已上架"),
    /**商品已下架*/
    GOODS_UNSOLD((short)2,"已下架"),
    /**商品审核不通过*/
    GOODS_REJECT((short)3,"已驳回");

    /**状态*/
    private final Short status;
    /**描述*/
    private final String depict;

    /**状态*/
    public Short getStatus() {
        return status;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }

    GoodsStatus(Short status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(Short status) {
        if (status != null) {
            for (GoodsStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

    /**判断商品是否待审核*/
    public static Boolean getIsAudit(LitemallGoods goods){
        return goods.getStatus().equals(GOODS_AUDIT.getStatus());
    }

    /**判断商品是否已上架*/
    public static Boolean getIsOnSale(LitemallGoods goods){
        return goods.getStatus().equals(GOODS_ON_SALE.getStatus());
    }

    /**判断商品是否已下架*/
    public static Boolean getIsUnsold(LitemallGoods goods){
        return goods.getStatus().equals(GOODS_UNSOLD.getStatus());
    }

    /**判断商品是否已驳回*/
    public static Boolean getIsReject(LitemallGoods goods){
        return goods.getStatus().equals(GOODS_REJECT.getStatus());
    }

}
