package org.ysling.litemall.db.enums;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账号等级
 * @author Ysling
 */
public enum UserLevel implements Serializable {

    /////////////////////////////////
    //       账号等级
    ////////////////////////////////

    /**普通用户*/
    USER_LEVEL_0((byte)0,"普通用户" , new BigDecimal("0.01") , 0),
    /**vip用户*/
    USER_LEVEL_1((byte)1,"vip用户" , new BigDecimal("0.02") ,100),
    /**高级vip用户*/
    USER_LEVEL_2((byte)2,"高级vip用户" ,new BigDecimal("0.04") , 1000);

    /**状态*/
    private final Byte status;
    /**描述*/
    private final String depict;
    /**分成比例*/
    private final BigDecimal ratio;
    /**分享人数*/
    private final Integer shareCount;

    /**状态*/
    public Byte getStatus() {
        return status;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }

    /**分成比例*/
    public BigDecimal getRatio() {
        return ratio;
    }

    /**分享人数*/
    public Integer getShareCount() {
        return shareCount;
    }

    UserLevel(Byte status, String depict , BigDecimal ratio , Integer shareCount) {
        this.ratio = ratio;
        this.status = status;
        this.depict = depict;
        this.shareCount = shareCount;
    }

    /**
     * 根据状态获取等级人数
     * @param count 数量
     * @return 返回描述
     */
    public static Byte parseCount(Integer count) {
        Byte status = 0;
        if (count != null) {
            for (UserLevel item : values()) {
                if (item.shareCount < count){
                    status = item.status;
                }
            }
            return status;
        }
        throw new IllegalStateException("status不支持");
    }

    /**
     * 根据状态获取比例
     * @param status 状态
     * @return 返回描述
     */
    public static BigDecimal parseRatio(Byte status) {
        if (status != null) {
            for (UserLevel item : values()) {
                if (item.status.equals(status)) {
                    return item.ratio;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(Byte status) {
        if (status != null) {
            for (UserLevel item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        return null;
    }

}
