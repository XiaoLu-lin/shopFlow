package org.ysling.litemall.db.enums;

import java.io.Serializable;

/**
 * 售后枚举
 * @author Ysling
 */
public enum AftersaleStatus implements Serializable {

    /////////////////////////////////
    //       售后状态
    ////////////////////////////////

    /**可申请*/
    STATUS_INIT((short)0,"可申请"),
    /**用户已申请*/
    STATUS_REQUEST((short)1,"已申请"),
    /**管理员审核通过*/
    STATUS_RECEPT((short)2,"审核通过"),
    /**管理员退款成功*/
    STATUS_REFUND((short)3,"退款成功"),
    /**管理员审核拒绝*/
    STATUS_REJECT((short)4,"审核拒绝"),
    /**用户已取消*/
    STATUS_CANCEL((short)5,"用户已取消");

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


    AftersaleStatus(Short status, String depict) {
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
            for (AftersaleStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

}
