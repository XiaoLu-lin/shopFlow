package org.ysling.shopflow.admin.web;
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

import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.tenant.body.TenantListBody;
import org.ysling.shopflow.admin.service.AdminPermissionService;
import org.ysling.shopflow.admin.service.AdminRoleService;
import org.ysling.shopflow.core.redis.config.RedisStartupRunner;
import org.ysling.shopflow.core.redis.writer.CustomRedisCacheWriter;
import org.ysling.shopflow.core.redis.writer.RedisConnectionHolder;
import org.ysling.shopflow.core.handler.DataSourceHandler;
import org.ysling.shopflow.core.tenant.handler.TenantContextHolder;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.weixin.config.WxStartupRunner;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.domain.ShopflowPermission;
import org.ysling.shopflow.db.domain.ShopflowRole;
import org.ysling.shopflow.db.domain.ShopflowTenant;
import org.ysling.shopflow.admin.service.AdminAdminService;
import org.ysling.shopflow.admin.service.AdminTenantService;
import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.util.List;


/**
 * 多租户管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/tenant")
@Validated
public class AdminTenantController {

    @Autowired
    private DataSourceHandler dataSourceHandler;
    @Autowired
    private AdminTenantService tenantService;
    @Autowired
    private AdminAdminService adminService;
    @Autowired
    private WxStartupRunner wxStartupRunner;
    @Autowired
    private RedisStartupRunner redisStartupRunner;
    @Autowired
    private CustomRedisCacheWriter redisCacheWriter;
    @Autowired
    private AdminRoleService roleService;
    @Autowired
    private AdminPermissionService permissionService;

    /**
     * 默认加密字符串
     */
    private final String encrypt = "******************";

    /**
     * 租户查询
     */
    @SaCheckPermission("admin:tenant:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "租户管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(TenantListBody body) {
        List<ShopflowTenant> tenantList = tenantService.querySelective(body);
        for (ShopflowTenant tenant :tenantList) {
            if (StringUtils.hasText(tenant.getUsername())){
                tenant.setUsername(encrypt);
            }
            if (StringUtils.hasText(tenant.getPassword())){
                tenant.setPassword(encrypt);
            }
        }
        return ResponseUtil.okList(tenantList);
    }

    /**
     * 添加租户
     */
    @SaCheckPermission("admin:tenant:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "租户管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowTenant tenant) {
        Object validate = tenantService.validate(tenant);
        if (validate != null){
            return validate;
        }
        if (tenantService.findAppid(tenant.getAppId()) != null){
            return ResponseUtil.fail("当前小程序Appid已存在");
        }
        if (tenantService.findAddress(tenant.getAddress()) != null){
            return ResponseUtil.fail("地址已存在");
        }
        //redis默认16个兄弟默认0占一个
        if (tenantService.count() >= 15){
            return ResponseUtil.fail("租户已满（限制15个租户）");
        }
        //添加租户
        if (tenantService.add(tenant) == 0){
            return ResponseUtil.addDataFailed();
        }
        //添加小程序配置
        wxStartupRunner.addWxConfig(tenant);
        //添加数据源
        dataSourceHandler.addDataSource(tenant);
        //添加redis库
        redisStartupRunner.setRedisFactory(tenant.getId());
        //初始化租户超级管理员
        this.tenantAdminInit(tenant.getId());
        return ResponseUtil.ok();
    }

    /**
     * 初始化租户超级管理员
     */
    private void tenantAdminInit(String tenantId){
        TenantContextHolder.setLocalTenantId(tenantId);
        //添加角色
        ShopflowRole role = new ShopflowRole();
        role.setName("超级管理员");
        role.setDepict("所有模块的权限");
        role.setEnabled(true);
        if (!roleService.save(role)){
            throw new RuntimeException("角色添加失败");
        }
        //添加角色权限
        ShopflowPermission permission = new ShopflowPermission();
        permission.setRoleId(role.getId());
        permission.setPermission("*");
        if (permissionService.add(permission) == 0){
            throw new RuntimeException("权限添加失败");
        }
        //添加管理员并赋予角色
        ShopflowAdmin admin = new ShopflowAdmin();
        admin.setUsername("admin123");
        admin.setPassword("admin123");
        admin.setRoleIds(new String[]{role.getId()});
        if (!adminService.saveAdmin(admin)){
            throw new RuntimeException("管理员添加失败");
        }
        TenantContextHolder.removeLocalTenantId();
    }


    /**
     * 修改租户
     */
    @SaCheckPermission("admin:tenant:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "租户管理"}, button = "修改")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowTenant tenant) {
        if (StringUtils.hasText(tenant.getUsername())){
            if (tenant.getUsername().equals(encrypt)){
                tenant.setUsername(null);
            }
        }
        if (StringUtils.hasText(tenant.getPassword())){
            if (tenant.getPassword().equals(encrypt)){
                tenant.setPassword(null);
            }
        }
        Object validate = tenantService.validate(tenant);
        if (validate != null){
            return validate;
        }
        if (tenantService.updateVersionSelective(tenant) == 0){
            return ResponseUtil.updatedDateExpired();
        }
        //添加小程序配置
        wxStartupRunner.addWxConfig(tenant);
        //添加数据源
        dataSourceHandler.addDataSource(tenant);
        //清除所有缓存
        redisCacheWriter.cleanTenant(tenant.getId());
        return ResponseUtil.ok();
    }

    /**
     * 删除租户
     */
    @SaCheckPermission("admin:tenant:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "租户管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        ShopflowTenant tenant = tenantService.findById(id);
        if (tenant == null){
            return ResponseUtil.fail("租户不存在");
        }
        //删除租户
        tenantService.deleteById(tenant.getId());
        //删除微信配置
        wxStartupRunner.remove(tenant.getAppId());
        //删除数据源
        dataSourceHandler.remove(tenant.getId());
        //清除所有缓存
        redisCacheWriter.cleanTenant(tenant.getId());
        //删除租户redis链接
        RedisConnectionHolder.removeRedisFactory(tenant.getId());
        return ResponseUtil.ok();
    }
}
