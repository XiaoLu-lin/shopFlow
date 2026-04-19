package org.ysling.litemall.admin.annotation.context;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.ysling.litemall.admin.annotation.entity.Permission;
import org.ysling.litemall.admin.annotation.handler.PermissionHandler;
import org.ysling.litemall.admin.annotation.entity.PermVo;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;


import java.util.*;

/**
 * 权限服务类
 * @author Ysling
 */
@Component
public class PermissionsContext {

    @Autowired
    private ApplicationContext context;

    /**默认系统权限*/
    private Set<String> systemPermissionsSet = null;
    private List<PermVo> systemPermissionList = null;
    /**租户系统权限*/
    private Set<String> tenantPermissionsSet = null;
    private List<PermVo> tenantPermissionList = null;
    /**系统权限map*/
    private HashMap<String, String> systemPermissionsMap = null;
    /**注解包路径*/
    private final String basicPackage = "org.ysling.litemall.admin";
    /**禁用的权限*/
    private final List<String> disabledPermission = Arrays.asList("/admin/log", "/admin/tenant");

    /**
     * 将系统权限转换成API
     * @param permissions 权限列表
     * @return 转换后Api列表
     */
    public Collection<String> toApi(Collection<String> permissions) {
        if (permissions == null){
            return new ArrayList<>();
        }
        HashSet<String> permissionSet = new HashSet<>(permissions);
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            List<Permission> systemPermissions = PermissionHandler.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getSaCheckPermission().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }
        Collection<String> apis = new HashSet<>();
        if (permissionSet.contains("*")){
            apis.addAll(systemPermissionsMap.values());
        }else {
            for (String perm : permissionSet) {
                apis.add(systemPermissionsMap.get(perm));
            }
        }
        //不是默认租户自动移除租户权限
        if (!TenantContextHolder.isDefaultId()){
            apis.removeIf(permission -> {
                for (String disabled :disabledPermission) {
                    if (permission.contains(disabled)){
                        return true;
                    }
                }
                return false;
            });
        }
        return apis;
    }

    /**
     * 获取权限列表
     */
    public Set<String> getSystemPermissionsSet() {
        this.initSystemPermissions();
        //不是默认租户自动移除租户权限
        if (TenantContextHolder.isDefaultId()){
            return systemPermissionsSet;
        }else {
            return tenantPermissionsSet;
        }
    }

    /**
     * 系统权限菜单
     */
    public List<PermVo> getSystemPermissionList() {
        this.initSystemPermissions();
        //不是默认租户自动移除租户权限
        if (TenantContextHolder.isDefaultId()){
            return systemPermissionList;
        }else {
            return tenantPermissionList;
        }
    }

    /**
     * 初始化权限
     */
    private void initSystemPermissions(){
        if (TenantContextHolder.isDefaultId()){
            if (systemPermissionsSet == null || systemPermissionList == null){
                List<Permission> permissions = this.getPermissions();
                systemPermissionList = PermissionHandler.listPermVo(permissions);
                systemPermissionsSet = PermissionHandler.listPermissionString(permissions);
            }
        }else {
            if (tenantPermissionsSet == null || tenantPermissionList == null){
                List<Permission> permissions = this.getPermissions();
                tenantPermissionList = PermissionHandler.listPermVo(permissions);
                tenantPermissionsSet = PermissionHandler.listPermissionString(permissions);
            }
        }
    }

    /**
     * 系统所有权限
     */
    private List<Permission> getPermissions(){
        List<Permission> permissions = PermissionHandler.listPermission(context, basicPackage);
        if (!TenantContextHolder.isDefaultId()){
            permissions.removeIf(permission -> {
                for (String disabled :disabledPermission) {
                    if (permission.getApi().contains(disabled)){
                        return true;
                    }
                }
                return false;
            });
        }
        return permissions;
    }

}
