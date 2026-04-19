package org.ysling.litemall.db.enums;

/**
 * 评论类型
 * @author Ysling
 */
public enum CommentType {

    /////////////////////////////////
    //  评论类型  0：动态评论，1：专题评论，2：店铺评论
    ////////////////////////////////

    /**动态评论*/
    TYPE_TIMELINE((short)0,"动态评论"),
    /**专题评论*/
    TYPE_TOPIC((short)1,"专题评论"),
    /**店铺评论*/
    TYPE_BRAND((short)2,"店铺评论");

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


    CommentType(Short status, String depict) {
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
            for (CommentType item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }
}
