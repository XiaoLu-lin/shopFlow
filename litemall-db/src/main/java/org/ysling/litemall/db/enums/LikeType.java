package org.ysling.litemall.db.enums;

/**
 * 点赞类型
 */
public enum LikeType {

    /////////////////////////////////
    //  点赞类型
    ////////////////////////////////

    /**动态点赞*/
    TYPE_TIMELINE((short)0,"动态点赞"),
    /**专题点赞*/
    TYPE_TOPIC((short)1,"专题点赞"),
    /**店铺点赞*/
    TYPE_BRAND((short)2,"店铺点赞"),
    /**评论点赞*/
    TYPE_COMMENT((short)3,"评论点赞");

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


    LikeType(Short status, String depict) {
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
            for (LikeType item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }
}
