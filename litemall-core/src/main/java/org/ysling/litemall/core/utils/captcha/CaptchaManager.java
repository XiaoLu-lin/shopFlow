package org.ysling.litemall.core.utils.captcha;
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

import org.ysling.litemall.core.redis.cache.RedisCacheService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 缓存系统中的验证码
 * @author Ysling
 */
public class CaptchaManager {

    /*** 验证码存储map线程安全*/
    public static Map<String, List<CaptchaItem>> captchaCodeCache = new ConcurrentHashMap<>();
    /*** 默认重发时间  单位分钟*/
    private static final Integer DEFAULT_RETRY_TIME = 1;
    /*** 默认过期时间  单位分钟*/
    private static final Integer DEFAULT_EXPIRE_TIME = 30;
    /*** 默认单日发送上限*/
    private static final Integer DEFAULT_MAX_SIZE = 10;
    /**是否使用redis做验证码缓存*/
    private static final Boolean REDIS_CAPTCHA = true;

    private static final String CAPTCHA_PREFIX = "CAPTCHA_CODE:";

    /**
     * 添加到redis缓存
     *
     * @param account     账号
     * @param code        验证码
     */
    public static boolean addRedisToCache(String account, String code) {
        List<Object> keys = RedisCacheService.keys(CAPTCHA_PREFIX + account + "*");
        if (keys.size() > 0){
            List<Object> items = RedisCacheService.multiGet(keys);
            for (Object item :items) {
                //未超过重发时间
                CaptchaItem captchaItem = (CaptchaItem)item;
                if (!captchaItem.getRetryTime().isBefore(LocalDateTime.now())){
                    throw new RuntimeException("验证码未超过重发时间");
                }
            }
        }

        // 创建验证码实体类
        CaptchaItem captchaItem = new CaptchaItem();
        // 设置重发时间
        captchaItem.setRetryTime(LocalDateTime.now().plusMinutes(DEFAULT_RETRY_TIME));
        // 设置过期时间
        captchaItem.setExpireTime(LocalDateTime.now().plusMinutes(DEFAULT_EXPIRE_TIME));
        // 添加账号
        captchaItem.setAccount(account);
        // 添加验证码
        captchaItem.setCode(code);

        RedisCacheService.put(CAPTCHA_PREFIX + account + ":" + code, captchaItem ,DEFAULT_EXPIRE_TIME.longValue(), TimeUnit.MINUTES);
        return false;
    }

    /**
     * 判断验证码是否正确
     * @param account 账号
     * @param code  验证码
     * @return   验证码是否有效
     */
    public static boolean isRedisCachedCaptcha(String account, String code) {
        String key = CAPTCHA_PREFIX + account + ":" + code;
        if (RedisCacheService.get(key) != null){
            return !RedisCacheService.remove(key);
        }
        return true;
    }


    /**
     * 添加到缓存
     *
     * @param account     账号
     * @param code        验证码
     */
    public static boolean addToCache(String account, String code) {
        if (REDIS_CAPTCHA){
            return addRedisToCache(account , code);
        }

        List<CaptchaItem> items = captchaCodeCache.get(account);
        if (items != null){
            for (CaptchaItem item :items) {
                //未超过重发时间
                if (!item.getRetryTime().isBefore(LocalDateTime.now())){
                    return true;
                }
                //超过过期时间删除
                if (item.getExpireTime().isBefore(LocalDateTime.now())){
                    items.remove(item);
                }
            }
        }else {
            items = Collections.synchronizedList(new ArrayList<>());
        }

        if (items.size() >= DEFAULT_MAX_SIZE){
            return true;
        }

        // 创建验证码实体类
        CaptchaItem captchaItem = new CaptchaItem();
        // 设置重发时间
        captchaItem.setRetryTime(LocalDateTime.now().plusMinutes(DEFAULT_RETRY_TIME));
        // 设置过期时间
        captchaItem.setExpireTime(LocalDateTime.now().plusMinutes(DEFAULT_EXPIRE_TIME));
        // 添加账号
        captchaItem.setAccount(account);
        // 添加验证码
        captchaItem.setCode(code);
        // 添加验证码实体类
        items.add(captchaItem);
        // 添加进map
        captchaCodeCache.put(account, items);
        return false;
    }

    /**
     * 判断验证码是否正确
     * @param account 账号
     * @param code  验证码
     * @return   验证码是否有效
     */
    public static boolean isCachedCaptcha(String account, String code) {
        if (REDIS_CAPTCHA){
            return isRedisCachedCaptcha(account , code);
        }

        //获取账号记录
        List<CaptchaItem> items = captchaCodeCache.get(account);
        //没有这个账号记录
        if (items == null) {
            return true;
        }
        //获取未过期的验证码
        for (CaptchaItem item :items) {
            //判断是否过期
            if (!item.getExpireTime().isBefore(LocalDateTime.now())){
                //判断是否相等
                if (Objects.equals(item.getCode() , code)){
                    //使用后删除验证码
                    items.remove(item);
                    //重新添加进map
                    captchaCodeCache.put(account, items);
                    return false;
                }
            }
        }
        return true;
    }
}
