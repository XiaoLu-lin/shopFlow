package org.ysling.litemall.db.enums;

import java.io.Serializable;

public enum UserGender implements Serializable {

    /////////////////////////////////
    //       用户性别
    ////////////////////////////////

    /**未知*/
    USER_GENDER_0((byte)0,"未知"),
    /**男*/
    USER_GENDER_1((byte)1,"男"),
    /**女*/
    USER_GENDER_2((byte)2,"女");

    /**状态*/
    private final Byte status;
    /**描述*/
    private final String depict;

    public Byte getStatus() {
        return status;
    }

    public String getDepict() {
        return depict;
    }

    UserGender(Byte status, String depict) {
        this.status = status;
        this.depict = depict;
    }


}
