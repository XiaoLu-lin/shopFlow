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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.role.body.RoleListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallRole;
import org.ysling.litemall.db.service.impl.RoleServiceImpl;
import java.util.List;
import java.util.Objects;

/**
 * 角色服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_role")
public class AdminRoleService extends RoleServiceImpl {


    /**
     * 参数校验
     * @param role 权限
     * @return 错误信息
     */
    public Object validate(LitemallRole role) {
        String name = role.getName();
        if (Objects.isNull(name)) {
            return ResponseUtil.fail("角色名称不能为空");
        }
        String depict = role.getDepict();
        if (Objects.isNull(depict)) {
            return ResponseUtil.fail("角色描述不能为空");
        }
        return null;
    }
    
    @Cacheable(sync = true)
    public List<LitemallRole> querySelective(RoleListBody body) {
        QueryWrapper<LitemallRole> wrapper = startPage(body);
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallRole.NAME , body.getName());
        }
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public boolean checkExist(String name) {
        QueryWrapper<LitemallRole> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallRole.NAME , name);
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallRole> queryAll() {
        return list();
    }

}
