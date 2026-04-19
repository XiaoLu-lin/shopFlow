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

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * token管理类
 */
@Slf4j
public class TokenJwtHelper {

	/**加密数字签名的秘钥*/
	private static final String SECRET = "X-Litemall-User-Token";
	/**签名是有谁生成*/
	private static final String ISSUSER = "Lite-mall-plus";
	/**签名的主题*/
	private static final String SUBJECT = "this is lite-mall-plus token";
	/**签名的观众*/
	private static final String AUDIENCE = "MINI-APP";

	/**
	 * 生成token
	 * @param value 需要加密的值
	 * @return 加密后的token
	 */
	public String createToken(String value, String name, Date expireDate){
		try {
		    Algorithm algorithm = Algorithm.HMAC256(SECRET);
		    Map<String, Object> map = new HashMap<>();
		    Date nowDate = new Date();
	        map.put("alg", "HS256");
	        map.put("typ", "JWT");
			return JWT.create()
				// 设置头部信息 Header
				.withHeader(map)
				// 设置 载荷 Payload
				.withClaim(name, value)
				// 签名是有谁生成
				.withIssuer(ISSUSER)
				// 签名的主题
				.withSubject(SUBJECT)
				// 签名的观众
				.withAudience(AUDIENCE)
				// 生成签名的时间
				.withIssuedAt(nowDate)
				// 签名过期的时间
				.withExpiresAt(expireDate)
				// 签名 Signature
				.sign(algorithm);
		} catch (JWTCreationException exception){
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析token
	 * @param token token
	 * @return 用户ID
	 */
	public String verifyTokenId(String token, String name) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256(SECRET);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer(ISSUSER)
		        .build();
		    DecodedJWT jwt = verifier.verify(token);
		    Map<String, Claim> claims = jwt.getClaims();
		    Claim claim = claims.get(name);
		    return claim.asString();
		} catch (Exception e){
			log.error("tokenException: "+e.getMessage());
			return null;
		}
	}

	/**
	 * 设置token过期时间
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param hour 时
	 * @param minute 分
	 * @param second 秒
	 * @return 时间
	 */
	public Date getExpireDate(int year, int month, int day, int hour, int minute, int second){
		Date date = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		if(year != 0){
			cal.add(Calendar.YEAR, year);
		}
		if(month != 0){
			cal.add(Calendar.MONTH, month);
		}
		if(day != 0){
			cal.add(Calendar.DATE, day);
		}
		if(hour != 0){
			cal.add(Calendar.HOUR_OF_DAY, hour);
		}
		if(minute != 0){
			cal.add(Calendar.MINUTE, minute);
		}
		if(second != 0){
			cal.add(Calendar.SECOND, second);
		}
		return cal.getTime();
	}
	
}
