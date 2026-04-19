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
import org.ysling.litemall.db.domain.LitemallGroupon;

import java.io.Serializable;

/**
 * 团购状态
 * @author Ysling
 */
public enum GrouponStatus implements Serializable {

    /////////////////////////////////
    //       团购状态
    ////////////////////////////////

    /**待开团（未支付）*/
    STATUS_NONE((short)0,"待开团"),
    /**团购中（已支付）*/
    STATUS_ON((short)1,"团购中"),
    /**团购失败（待退款）*/
    STATUS_FAIL((short)2,"团购失败"),
    /**团购成功（待发货）*/
    STATUS_SUCCEED((short)3,"团购成功"),
    /**团购取消（未支付）*/
    STATUS_CANCEL((short)4,"团购取消");

    /**
     * 是否待开团
     */
    public static Boolean isNone(LitemallGroupon groupon){
        return groupon.getStatus().equals(STATUS_NONE.getStatus());
    }
    /**
     * 团购中（已支付）
     */
    public static Boolean isOn(LitemallGroupon groupon){
        return groupon.getStatus().equals(STATUS_ON.getStatus());
    }
    /**
     * 团购失败（待退款）
     */
    public static Boolean isFail(LitemallGroupon groupon){
        return groupon.getStatus().equals(STATUS_FAIL.getStatus());
    }
    /**
     * 团购成功（待发货）
     */
    public static Boolean isSucceed(LitemallGroupon groupon){
        return groupon.getStatus().equals(STATUS_SUCCEED.getStatus());
    }
    /**
     * 团购取消（未支付）
     */
    public static Boolean isCancel(LitemallGroupon groupon){
        return groupon.getStatus().equals(STATUS_CANCEL.getStatus());
    }

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

    GrouponStatus(Short status, String depict) {
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
            for (GrouponStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }



}
