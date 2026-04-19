package org.ysling.litemall.db.enums;

/**
 * 收藏类型
 * @author Ysling
 */
public enum CollectType {

    /////////////////////////////////
    //  收藏类型  0：商品收藏，1：专题收藏，
    ////////////////////////////////

    /**商品收藏*/
    TYPE_GOODS((byte)0,"商品收藏"),
    /**专题收藏*/
    TYPE_TOPIC((byte)1,"专题收藏");

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


    CollectType(Byte status, String depict) {
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
            for (CollectType item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }
}
