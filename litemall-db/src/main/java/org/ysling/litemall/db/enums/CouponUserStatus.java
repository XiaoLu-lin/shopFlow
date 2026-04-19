package org.ysling.litemall.db.enums;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import java.io.Serializable;

/**
 * 用户优惠券状态
 */
public enum CouponUserStatus implements Serializable {

    /////////////////////////////////
    //       用户优惠券状态
    ////////////////////////////////

    /**未使用*/
    STATUS_USABLE((short)0,"未使用"),
    /**已使用*/
    STATUS_USED((short)1,"已使用"),
    /**已过期*/
    STATUS_EXPIRED((short)2,"已过期"),
    /**已下架*/
    STATUS_OUT((short)3,"已下架");


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


    CouponUserStatus(Short status, String depict) {
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
            for (CouponUserStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }
}
