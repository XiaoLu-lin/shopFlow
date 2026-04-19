package org.ysling.litemall.core.weixin.enums;

/**
 * 敏感词校验
 * 命中标签枚举值
 */
public enum MsgSecCheckType {


    /////////////////////////////////
    //       敏感词类型
    ////////////////////////////////

    /**正常*/
    TYPE_100("100","正常"),
    /**广告*/
    TYPE_10001("10001","广告"),
    /**时政*/
    TYPE_20001("20001","时政"),
    /**色情*/
    TYPE_20002("20002","色情"),
    /**辱骂*/
    TYPE_20003("20003","辱骂"),
    /**违法犯罪*/
    TYPE_20006("20006","违法犯罪"),
    /**欺诈*/
    TYPE_20008("20008","欺诈"),
    /**低俗*/
    TYPE_20012("20012","低俗"),
    /**版权*/
    TYPE_20013("20013","版权"),
    /**其他*/
    TYPE_21000("21000","其他");

    /**状态*/
    private final String status;
    /**描述*/
    private final String depict;

    public String getStatus() {
        return status;
    }

    public String getDepict() {
        return depict;
    }

    MsgSecCheckType(String status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(String status) {
        if (status != null) {
            for (MsgSecCheckType item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        return "";
    }
}
