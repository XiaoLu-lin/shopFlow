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
 * 团购状态
 * @author Ysling
 */
public enum GrouponRuleStatus implements Serializable {

    /////////////////////////////////
    //       团购规则状态
    ////////////////////////////////

    /**团购规则待上线*/
    RULE_STATUS_OFF((short)0,"团购待上线"),
    /**团购规则正常*/
    RULE_STATUS_ON((short)1,"团购正常"),
    /**团购规则到期下线*/
    RULE_STATUS_DOWN_EXPIRE((short)2,"到期下线"),
    /**团购规则提前下线*/
    RULE_STATUS_DOWN_ADMIN((short)3,"提前下线");


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

    GrouponRuleStatus(Short status, String depict) {
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
            for (GrouponRuleStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        throw new IllegalStateException("status不支持");
    }



}
