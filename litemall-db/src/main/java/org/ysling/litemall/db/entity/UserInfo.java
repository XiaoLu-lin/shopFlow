package org.ysling.litemall.db.entity;
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
import java.time.LocalDateTime;

/**
 * 用户信息
 * @author Ysling
 */
@Data
public class UserInfo implements Serializable {

    /**
     * 微信openid
     */
    private String openId;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 用户手机号码
     */
    private String mobile;
    /**
     * 真实姓名
     */
    private String trueName;
    /**
     * 国家
     */
    private String country;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 性别
     */
    private Byte gender;
    /**
     * 语言
     */
    private String language;
    /**
     * 生日
     */
    private LocalDateTime birthday;
    /**
     * uni登陆ID
     */
    private String unionId;
    /**
     * 个人分享图片
     */
    private String shareUrl;


}
