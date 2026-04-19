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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallPermission;
import org.ysling.litemall.db.service.impl.PermissionServiceImpl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限表
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_permission")
public class AdminPermissionService extends PermissionServiceImpl {

    
    @Cacheable(sync = true)
    public Set<String> queryByRoleId(String roleId) {
        Set<String> permissions = new HashSet<>();
        if(roleId == null){
            return permissions;
        }
        QueryWrapper<LitemallPermission> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallPermission.ROLE_ID , roleId);
        for(LitemallPermission permission : list(wrapper)){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    
    @Cacheable(sync = true)
    public Set<String> queryByRoleId(List<String> roleIds) {
        Set<String> permissions = new HashSet<>();
        if(roleIds == null || roleIds.isEmpty()){
            return permissions;
        }
        QueryWrapper<LitemallPermission> wrapper = new QueryWrapper<>();
        wrapper.in(LitemallPermission.ROLE_ID , roleIds);
        for(LitemallPermission permission : list(wrapper)){
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    
    @Cacheable(sync = true)
    public boolean checkSuperPermission(String roleId) {
        if(roleId == null){
            return false;
        }
        QueryWrapper<LitemallPermission> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallPermission.ROLE_ID , roleId);
        wrapper.eq(LitemallPermission.PERMISSION , "*");
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public boolean checkSuperPermission(List<String> roleIds) {
        if(roleIds == null || roleIds.isEmpty()){
            return false;
        }
        QueryWrapper<LitemallPermission> wrapper = new QueryWrapper<>();
        wrapper.in(LitemallPermission.ROLE_ID , roleIds);
        wrapper.eq(LitemallPermission.PERMISSION , "*");
        return exists(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByRoleId(String roleId) {
        UpdateWrapper<LitemallPermission> wrapper = new UpdateWrapper<>();
        wrapper.eq(LitemallPermission.ROLE_ID , roleId);
        remove(wrapper);
    }


}