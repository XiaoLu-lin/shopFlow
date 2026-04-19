package org.ysling.litemall.admin.service;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.admin.body.AdminListBody;
import org.ysling.litemall.core.utils.RegexUtil;
import org.ysling.litemall.core.utils.bcrypt.CryptoUtil;
import org.ysling.litemall.core.utils.response.ResponseStatus;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallAdmin;
import org.ysling.litemall.db.service.impl.AdminServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_admin")
public class AdminAdminService extends AdminServiceImpl {


    public Object validate(LitemallAdmin admin) {
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
    public boolean saveAdmin(LitemallAdmin admin) {
        admin.setPassword(CryptoUtil.encode(admin.getPassword()));
        return saveOrUpdate(admin);
    }

    @Cacheable(sync = true)
    public List<LitemallAdmin> findAdmin(String username){
        QueryWrapper<LitemallAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAdmin.USERNAME , username);
        return queryAll(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<LitemallAdmin> findByTenantId(String tenantId) {
        QueryWrapper<LitemallAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAdmin.TENANT_ID , tenantId);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<LitemallAdmin> querySelective(AdminListBody body) {
        QueryWrapper<LitemallAdmin> wrapper = startPage(body);
        if (StringUtils.hasText(body.getMail())) {
            wrapper.like(LitemallAdmin.MAIL , body.getMail());
        }
        if (StringUtils.hasText(body.getMobile())) {
            wrapper.like(LitemallAdmin.MOBILE , body.getMobile());
        }
        if (StringUtils.hasText(body.getUsername())) {
            wrapper.like(LitemallAdmin.USERNAME , body.getUsername());
        }
        return queryAll(wrapper);
    }


}
