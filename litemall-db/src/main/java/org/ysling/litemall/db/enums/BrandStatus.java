package org.ysling.litemall.db.enums;

import java.io.Serializable;

/**
 * 店铺状态
 * @author Ysling
 */
public enum BrandStatus implements Serializable {

    /////////////////////////////////
    //       店铺状态
    ////////////////////////////////

    /**正常*/
    STATUS_NORMAL((byte)0,"正常"),
    /**禁用*/
    STATUS_DISABLED((byte)1,"禁用"),
    /**注销*/
    STATUS_OUT((byte)2,"注销");

    /**状态*/
    private final Byte status;
    /**描述*/
    private final String depict;

    /**状态*/
    public Byte getStatus() {
        return status;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }

    BrandStatus(Byte status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 状态判断
     */
    public Boolean equals(Byte status){
        return this.getStatus().equals(status);
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(Byte status) {
        if (status != null) {
            for (BrandStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }
}
