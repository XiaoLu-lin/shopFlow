package org.ysling.litemall.db.enums;

import org.ysling.litemall.db.domain.LitemallUser;

import java.io.Serializable;
import java.util.Objects;

/**
 * 账号状态
 * @author Ysling
 */
public enum UserStatus implements Serializable {

    /////////////////////////////////
    //       账号状态
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

    UserStatus(Byte status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(Byte status) {
        if (status != null) {
            for (UserStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }

    /**判断用户是否正常*/
    public static Boolean isNormal(LitemallUser user){
        return Objects.equals(user.getStatus(), UserStatus.STATUS_NORMAL.getStatus());
    }

}
