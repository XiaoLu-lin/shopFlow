package org.ysling.litemall.core.notify.model;
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

/**
 * 短信枚举类
 * @author Ysling
 */
public enum SmsType {
    /**腾讯短信*/
    TENCENT("tencent"),
    /**阿里短信*/
    ALIYUN("aliyun");

    private final String type;

    SmsType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
