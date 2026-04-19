package org.ysling.litemall.admin.web;
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
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.annotation.context.PermissionsContext;
import org.ysling.litemall.admin.service.AdminAdminService;
import org.ysling.litemall.admin.service.AdminPermissionService;
import org.ysling.litemall.admin.service.AdminRoleService;
import org.ysling.litemall.admin.model.role.body.RoleListBody;
import org.ysling.litemall.admin.model.role.body.RoleUpdatePermissionsBody;
import org.ysling.litemall.admin.model.role.result.PermissionsResult;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.db.domain.LitemallAdmin;
import org.ysling.litemall.db.domain.LitemallPermission;
import org.ysling.litemall.db.domain.LitemallRole;
import org.ysling.litemall.db.entity.BaseOption;
import org.ysling.litemall.admin.annotation.entity.PermVo;

import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;
import java.util.*;
/**
 * 角色管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@Validated
public class AdminRoleController {

    @Autowired
    private AdminRoleService roleService;
    @Autowired
    private AdminAdminService adminService;
    @Autowired
    private AdminPermissionService permissionService;
    @Autowired
    private PermissionsContext permissionsContext;

    /**
     * 角色列表查询
     */
    @SaCheckPermission("admin:role:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色列表")
    @GetMapping("/list")
    public Object list(RoleListBody body) {
        return ResponseUtil.okList(roleService.querySelective(body));
    }

    /**
     * 查询角色map  [{角色id,角色名称}]
     */
    @GetMapping("/options")
    public Object options() {
        List<LitemallRole> roleList = roleService.queryAll();
        List<BaseOption> options = new ArrayList<>(roleList.size());
        for (LitemallRole role : roleList) {
            BaseOption option = new BaseOption();
            option.setValue(role.getId());
            option.setLabel(role.getName());
            options.add(option);
        }
        return ResponseUtil.okList(options);
    }

    /**
     * 查询角色详情
     * @param id 角色ID
     */
    @SaCheckPermission("admin:role:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(roleService.findById(id));
    }


    /**
     * 角色添加
     * @param role 角色详情
     */
    @SaCheckPermission("admin:role:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody LitemallRole role) {
        Object error = roleService.validate(role);
        if (error != null) {
            return error;
        }
        if (roleService.checkExist(role.getName())) {
            return ResponseUtil.fail("角色已经存在");
        }
        if (roleService.add(role) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 角色编辑
     * @param role 角色信息
     */
    @SaCheckPermission("admin:role:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody LitemallRole role) {
        Object error = roleService.validate(role);
        if (error != null) {
            return error;
        }
        if (roleService.updateVersionSelective(role) == 0){
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok();
    }

    /**
     * 角色删除
     * @param id 角色id
     */
    @SaCheckPermission("admin:role:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (permissionService.checkSuperPermission(id)) {
            return ResponseUtil.fail( "当前角色的超级权限不能删除");
        }
        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        List<LitemallAdmin> adminList = adminService.list();
        for (LitemallAdmin admin : adminList) {
            String[] roleIds = admin.getRoleIds();
            for (String roleId : roleIds) {
                if (id.equals(roleId)) {
                    return ResponseUtil.fail( "当前角色存在管理员，不能删除");
                }
            }
        }
        if (roleService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


    /**
     * 管理员的权限情况
     * @param roleId 角色id
     * @return 系统所有权限列表、角色权限、管理员已分配权限
     */
    @SaCheckPermission("admin:role:permission:get")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限详情")
    @GetMapping("/permissions")
    public Object getPermissions(@JsonBody String roleId) {
        // 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        Set<String> assignedPermissions;
        if (permissionService.checkSuperPermission(roleId)) {
            assignedPermissions = permissionsContext.getSystemPermissionsSet();
        } else {
            assignedPermissions = permissionService.queryByRoleId(roleId);
        }
        String adminId = StpUtil.getLoginIdAsString();
        LitemallAdmin admin = adminService.findById(adminId);
        String[] roles = admin.getRoleIds();
        List<String> roleIds = Arrays.asList(roles);
        Set<String> curPermissions = new HashSet<>();
        if (!permissionService.checkSuperPermission(roleIds)) {
            curPermissions = permissionService.queryByRoleId(roleIds);
        }
        PermissionsResult result = new PermissionsResult();
        result.setSystemPermissions(permissionsContext.getSystemPermissionList());
        result.setAssignedPermissions(assignedPermissions);
        result.setCurPermissions(curPermissions);
        return ResponseUtil.ok(result);
    }

    /**
     * 更新管理员权限
     * @param body  参数
     */
    @SaCheckPermission("admin:role:permission:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限变更")
    @PostMapping("/permissions")
    public Object updatePermissions(@Valid @RequestBody RoleUpdatePermissionsBody body) {
        // 如果修改的角色是超级权限，则拒绝修改。
        if (permissionService.checkSuperPermission(body.getRoleId())) {
            return ResponseUtil.fail( "当前角色的超级权限不能变更");
        }
        // 先删除旧的权限，再更新新的权限
        permissionService.deleteByRoleId(body.getRoleId());
        // 整合权限
        HashSet<LitemallPermission> permissionSet = new HashSet<>();
        for (String permission : body.getPermissions()) {
            LitemallPermission litemallPermission = new LitemallPermission();
            litemallPermission.setRoleId(body.getRoleId());
            litemallPermission.setPermission(permission);
            permissionSet.add(litemallPermission);
        }
        permissionService.batchAdd(new ArrayList<>(permissionSet));
        return ResponseUtil.ok();
    }

}
