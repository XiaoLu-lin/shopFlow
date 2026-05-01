package org.ysling.shopflow.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.service.impl.AdminServiceImpl;
import java.util.List;

/**
 * 管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_admin")
public class WxAdminService extends AdminServiceImpl {

    
    @Cacheable(sync = true)
    public ShopflowAdmin findByOpenId(String openId) {
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.OPENID , openId);
        return getOne(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowAdmin> findByTenantId(String tenantId) {
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.TENANT_ID , tenantId);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<ShopflowAdmin> findAdmin(String username){
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.USERNAME , username);
        return queryAll(wrapper);
    }


}
