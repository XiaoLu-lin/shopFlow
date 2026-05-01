package org.ysling.shopflow.admin.service;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.admin.body.AdminListBody;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.response.ResponseStatus;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.service.impl.AdminServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_admin")
public class AdminAdminService extends AdminServiceImpl {


    public Object validate(ShopflowAdmin admin) {
        String name = admin.getUsername();
        if (Objects.isNull(name) || name.length() < 6) {
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0110);
        }
        if (RegexUtil.isQQMail(admin.getMail())){
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0153);
        }
        if (!RegexUtil.isMobileSimple(admin.getMobile())){
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0151);
        }
        String password = admin.getPassword();
        if (Objects.isNull(password) || password.length() < 6) {
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0122);
        }
        return null;
    }

    @CacheEvict(allEntries = true)
    public boolean saveAdmin(ShopflowAdmin admin) {
        admin.setPassword(CryptoUtil.encode(admin.getPassword()));
        return saveOrUpdate(admin);
    }

    @Cacheable(sync = true)
    public List<ShopflowAdmin> findAdmin(String username){
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.USERNAME , username);
        return queryAll(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<ShopflowAdmin> findByTenantId(String tenantId) {
        QueryWrapper<ShopflowAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAdmin.TENANT_ID , tenantId);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<ShopflowAdmin> querySelective(AdminListBody body) {
        QueryWrapper<ShopflowAdmin> wrapper = startPage(body);
        if (StringUtils.hasText(body.getMail())) {
            wrapper.like(ShopflowAdmin.MAIL , body.getMail());
        }
        if (StringUtils.hasText(body.getMobile())) {
            wrapper.like(ShopflowAdmin.MOBILE , body.getMobile());
        }
        if (StringUtils.hasText(body.getUsername())) {
            wrapper.like(ShopflowAdmin.USERNAME , body.getUsername());
        }
        return queryAll(wrapper);
    }


}
