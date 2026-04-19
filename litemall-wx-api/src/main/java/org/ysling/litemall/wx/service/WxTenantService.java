package org.ysling.litemall.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.token.TokenManager;
import org.ysling.litemall.core.weixin.config.WxProperties;
import org.ysling.litemall.db.domain.LitemallTenant;
import org.ysling.litemall.db.service.impl.TenantServiceImpl;

/**
 * 多租户服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_tenant")
public class WxTenantService extends TenantServiceImpl {


    @Autowired
    private WxProperties properties;

    
    @Cacheable(sync = true)
    public LitemallTenant findAppid(String appid) {
        QueryWrapper<LitemallTenant> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallTenant.APP_ID , appid);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallTenant findAddress(String address) {
        QueryWrapper<LitemallTenant> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallTenant.ADDRESS , address);
        return getOne(wrapper);
    }

    /**
     * 获取租户密钥
     * @return 获取租户密钥
     */
    public String getTenant(String appid) {
        if (!StringUtils.hasText(appid)){
            throw new RuntimeException("初始化失败，请退出重新进入");
        }
        //判断是否是默认租户appid
        if (appid.equals(properties.getAppId())){
            String tenantId = TenantContextHolder.getDefaultId();
            TenantContextHolder.setLocalTenantId(tenantId);
            return TokenManager.createTenantToken(tenantId);
        }
        //根据appid获取租户
        LitemallTenant tenant = findAppid(appid);
        if (tenant == null){
            //判断是否添加的有多租户
            if (count() == 0){
                String tenantId = TenantContextHolder.getDefaultId();
                TenantContextHolder.setLocalTenantId(tenantId);
                return TokenManager.createTenantToken(tenantId);
            }
            //有多租户appid不存在
            throw new RuntimeException("初始化失败，请退出重新进入");
        }
        //根据租户ID生成token并返回
        TenantContextHolder.setLocalTenantId(tenant.getId());
        return TokenManager.createTenantToken(tenant.getId());
    }


}
