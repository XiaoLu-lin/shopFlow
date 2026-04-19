package org.ysling.litemall.core.tenant.handler;
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
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.tenant.enums.IgnoreRequest;
import org.ysling.litemall.core.utils.http.GlobalWebUtil;
import org.ysling.litemall.core.utils.token.TokenManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多租户上下文工具类
 * @author Ysling
 */
public class TenantContextHolder {

    /**租户ID 线程独立，可解决并发问题*/
    private static final ThreadLocal<String> LOCAL_TENANT_ID = new ThreadLocal<>();
    /**租户ID与微信appid映射*/
    private static final Map<String, String> WX_CONFIG_MAP = new ConcurrentHashMap<>();
    /**租户请求头*/
    private static final String TENANT_ID_HEADER = "X-Litemall-TenantId";
    /**默认租户ID*/
    private static final String TENANT_ID_DEFAULT = "0";
    /**默认租户ID*/
    public static String getDefaultId() {
        return TENANT_ID_DEFAULT;
    }
    /**判断是否是默认ID*/
    public static Boolean isDefaultId(String tenantId){
        return TENANT_ID_DEFAULT.equals(tenantId);
    }
    /**判断是否是默认ID*/
    public static Boolean isDefaultId(){
        return TENANT_ID_DEFAULT.equals(LOCAL_TENANT_ID.get());
    }
    /**设置默认租户ID*/
    public static void setDefaultId() {
        setLocalTenantId(TENANT_ID_DEFAULT);
    }

    /**
     * 添加微信配置映射
     * @param tenantId      租户ID
     * @param miniAppId     小程序appid
     */
    public static void setWxConfigMap(String tenantId, String miniAppId){
        WX_CONFIG_MAP.put(tenantId, miniAppId);
    }

    /**
     * 获取appid
     * @param tenantId 租户ID
     */
    public static String getWxAppid(String tenantId){
        return WX_CONFIG_MAP.get(tenantId);
    }

    /**
     * 获取当前线程租户
     * @return 当前租户id
     */
    public static String getLocalTenantId(){
        if (LOCAL_TENANT_ID.get() == null){
            setLocalTenantId(returnTenantId());
        }
        if (LOCAL_TENANT_ID.get() == null){
            return setLocalTenantId(TENANT_ID_DEFAULT);
        }
        return LOCAL_TENANT_ID.get();
    }

    /**
     * 清除当前租户
     */
    public static void removeLocalTenantId() {
        //清除多租户
        LOCAL_TENANT_ID.remove();
        //清除微信配置
        WxMaConfigHolder.remove();
        //清除多数据源
        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 设置当前线程租户
     * @param tenantId 当前租户id
     * @return 当前租户id
     */
    public static String setLocalTenantId(String tenantId){
        //切换租户
        LOCAL_TENANT_ID.remove();
        LOCAL_TENANT_ID.set(tenantId);
        if (tenantId != null){
            //切换数据源
            DynamicDataSourceContextHolder.clear();
            DynamicDataSourceContextHolder.push(tenantId);
            //获取租户与微信小程序映射appid
            String miniAppId = WX_CONFIG_MAP.get(tenantId);
            if (miniAppId != null){
                WxMaConfigHolder.remove();
                WxMaConfigHolder.set(miniAppId);
            }
        }
        return tenantId;
    }

    /**
     * 从请求中获取到token，从请求头中解析出tenantId
     * @return  租户ID
     */
    public static String returnTenantId() {
        return returnTenantId(GlobalWebUtil.getRequest());
    }

    /**
     * 从请求中获取到token，从请求头中解析出tenantId
     * @return  租户ID
     */
    public static String returnTenantId(HttpServletRequest request) {
        //获取当前请求如果没有请求则是内部调用返回默认租户
        if (Objects.isNull(request)){
            return TENANT_ID_DEFAULT;
        }
        //获取请求地址如果为白名单地址则返回默认租户
        String requestUri = request.getRequestURI();
        if (IgnoreRequest.parseValue(requestUri)){
            return TENANT_ID_DEFAULT;
        }
        //获取请求头如果未携带请求头则返回null
        String decode = request.getHeader(TENANT_ID_HEADER);
        String tenantId = TokenManager.getTenantId(decode);
        if (!StringUtils.hasText(tenantId)){
            return null;
        }
        return tenantId;
    }

}
