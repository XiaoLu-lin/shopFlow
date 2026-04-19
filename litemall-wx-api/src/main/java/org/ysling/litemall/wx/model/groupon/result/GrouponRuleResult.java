package org.ysling.litemall.wx.model.groupon.result;
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

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class GrouponRuleResult implements Serializable {

    private String id;

    private String name;

    private String brief;

    private String picUrl;

    private BigDecimal counterPrice;

    private BigDecimal retailPrice;

    private BigDecimal grouponPrice;

    private BigDecimal grouponDiscount;

    private Integer grouponMember;

    private LocalDateTime expireTime;
}
