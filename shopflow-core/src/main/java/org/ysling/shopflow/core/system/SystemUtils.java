package org.ysling.shopflow.core.system;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import cn.hutool.core.convert.Convert;
import org.ysling.shopflow.core.redis.cache.RedisCacheService;
import org.ysling.shopflow.core.utils.BeanUtil;
import org.ysling.shopflow.db.domain.ShopflowSystem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统设置
 * @author Ysling
 */
public class SystemUtils implements Serializable {

    /**
     * redis存储前缀
     */
    private static final String CONFIG_PREFIX = "SYSTEM_CONFIG:";

    /**
     * 分组列表
     */
    private static ArrayList<ConfigGroup> configGroups = new ArrayList<>();

    /**
     * 获取分组列表
     */
    public static ArrayList<ConfigGroup> getConfigGroups() {
        return configGroups;
    }

    /**
     * 添加分组列表
     */
    public static void setConfigGroups(ArrayList<ConfigGroup> configGroups) {
        SystemUtils.configGroups = configGroups;
    }

    /**
     * 将系统配置根据租户写入redis
     */
    public static void updateConfig(ShopflowSystem system) {
        RedisCacheService.putHash(CONFIG_PREFIX, system.getName(), system);
    }

    /**
     * 将系统配置根据租户写入redis
     */
    public static void updateConfigs(Map<String, ShopflowSystem> data) {
        HashMap<Object, Object> hashMap = new HashMap<>(data);
        RedisCacheService.putAllHash(CONFIG_PREFIX, hashMap);
    }

    /**
     * 从redis中获取租户配置
     */
    public static synchronized ShopflowSystem getSystem(String keyName) {
        Object value = RedisCacheService.getHash(CONFIG_PREFIX, keyName);
        if (value == null){
            BeanUtil.getBean(SystemService.class).initConfig();
        }
        return (ShopflowSystem) RedisCacheService.getHash(CONFIG_PREFIX, keyName);
    }

    /**
     * 从redis中获取租户配置
     */
    public static String getConfig(String keyName) {
        return getSystem(keyName).getValue();
    }

    /**
     * 将从redis获取的对象转为Long
     */
    public static Long getConfigLong(String keyName) {
        return Long.parseLong(getConfig(keyName));
    }

    /**
     * 将从redis获取的对象转为Integer
     */
    public static Integer getConfigInt(String keyName) {
        return Integer.parseInt(getConfig(keyName));
    }

    /**
     * 将从redis获取的对象转为Boolean
     */
    public static Boolean getConfigBoolean(String keyName) {
        return Boolean.valueOf(getConfig(keyName));
    }

    /**
     * 将从redis获取的对象转为BigDecimal
     */
    public static BigDecimal getConfigBigDec(String keyName) {
        return new BigDecimal(getConfig(keyName));
    }

    /**
     * 校验参数类型是否正确
     * @param name      配置名称
     * @param value     配置值
     */
    public static Object checkValue(String name, String value){
        try {
            ShopflowSystem system = getSystem(name);
            Class<?> objectType = Class.forName(system.getObjectType());
            return Convert.convert(objectType, value);
        }catch (Exception e){
            throw new RuntimeException("配置值不正确");
        }
    }

}