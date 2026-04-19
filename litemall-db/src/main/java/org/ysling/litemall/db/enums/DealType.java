package org.ysling.litemall.db.enums;

import java.io.Serializable;

/**
 * 交易记录类型
 * @author Ysling
 */
public enum DealType implements Serializable {

    /////////////////////////////////
    //       交易记录类型
    ////////////////////////////////

    /**订单抵扣*/
    TYPE_ORDER((short)0,"订单抵扣"),
    /**赏金奖励*/
    TYPE_REWARD((short)1,"赏金奖励"),
    /**分享奖励*/
    TYPE_SHARE((short)2,"分享奖励"),
    /**系统设置*/
    TYPE_SYSTEM((short)3,"系统设置"),
    /**手动提现*/
    TYPE_DRAW_MONEY((short)4,"手动提现"),
    /**订单取消*/
    TYPE_ORDER_CANCEL((short)5,"订单取消"),
    /**店铺奖励*/
    TYPE_BRAND((short)6,"店铺奖励");

    /**状态*/
    private final Short type;
    /**描述*/
    private final String depict;

    /**类型*/
    public Short getType() {
        return type;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }

    DealType(Short type, String depict) {
        this.type = type;
        this.depict = depict;
    }

    /**
     * 状态判断
     */
    public Boolean equals(Short status){
        return this.getType().equals(status);
    }

    /**
     * 根据状态获取描述
     * @param type 类型
     * @return 返回描述
     */
    public static String parseValue(Short type) {
        if (type != null) {
            for (DealType item : values()) {
                if (item.type.equals(type)) {
                    return item.depict;
                }
            }
        }
        return "";
    }

}
