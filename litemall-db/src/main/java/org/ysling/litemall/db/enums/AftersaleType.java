package org.ysling.litemall.db.enums;

import java.io.Serializable;

/**
 * 售后枚举
 * @author Ysling
 */
public enum AftersaleType implements Serializable {

    /////////////////////////////////
    //       售后类型
    ////////////////////////////////

    /**未收货退款*/
    TYPE_GOODS_MISS((short)0,"未收货退款"),
    /**已收货（无需退货）退款*/
    TYPE_GOODS_NEEDLESS((short)1,"已收货退款"),
    /**退货退款*/
    TYPE_GOODS_REQUIRED((short)2,"退货退款");

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


    AftersaleType(Short status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 状态判断
     */
    public Boolean equals(Short status){
        return this.getStatus().equals(status);
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(Short status) {
        if (status != null) {
            for (AftersaleType item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

}
