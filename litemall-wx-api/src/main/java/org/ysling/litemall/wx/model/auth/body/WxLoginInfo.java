package org.ysling.litemall.wx.model.auth.body;
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
import org.ysling.litemall.db.entity.UserInfo;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class WxLoginInfo implements Serializable {

    /**
     * 微信授权code
     */
    private String wxCode;
    /**
     * 手机授权code
     */
    private String phoneCode;
    /**
     * 邀请者
     */
    private String inviter;
    /**
     * 用户信息
     */
    private UserInfo userInfo;


}
