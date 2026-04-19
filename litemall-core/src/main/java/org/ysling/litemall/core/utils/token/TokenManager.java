package org.ysling.litemall.core.utils.token;
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


import java.util.Date;

/**
 * 维护token
 */
public class TokenManager {
    
    //用户ID的token名称
    private static final String USER_ID_NAME = "userId";
    //多租户ID的token名称
    private static final String TENANT_ID_NAME = "tenantId";

    /**
     * 生成用户token
     */
	public static String createUserToken(String userId) {
        TokenJwtHelper jwtHelper = new TokenJwtHelper();
        Date expireDate = jwtHelper.getExpireDate(1,0,0,0,0,0);
        return jwtHelper.createToken(userId, USER_ID_NAME, expireDate);
    }

    /**
     * 根据token获取用户id
     */
    public static String getUserId(String token) {
    	TokenJwtHelper jwtHelper = new TokenJwtHelper();
        return jwtHelper.verifyTokenId(token, USER_ID_NAME);
    }

    /**
     * 生成租户token
     */
    public static String createTenantToken(String tenantId) {
        TokenJwtHelper jwtHelper = new TokenJwtHelper();
        Date expireDate = jwtHelper.getExpireDate(0,0,1,0,0,0);
        return jwtHelper.createToken(tenantId, TENANT_ID_NAME,expireDate);
    }

    /**
     * 根据token获取租户id
     */
    public static String getTenantId(String token) {
        TokenJwtHelper jwtHelper = new TokenJwtHelper();
        return jwtHelper.verifyTokenId(token, TENANT_ID_NAME);
    }


}
