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
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.admin.body.AdminListBody;
import org.ysling.shopflow.admin.model.admin.result.AdminListResult;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.handler.ActionLogHandler;
import org.ysling.shopflow.core.utils.response.ResponseStatus;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.admin.service.AdminAdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminAdminController {
    
    @Autowired
    private AdminAdminService adminService;
    
    /**
     * 查询
     */
    @SaCheckPermission("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(AdminListBody body) {
        List<ShopflowAdmin> adminList = adminService.querySelective(body);
        ArrayList<AdminListResult> resultList = new ArrayList<>();
        for (ShopflowAdmin admin :adminList) {
            AdminListResult result = new AdminListResult();
            BeanUtil.copyProperties(admin , result);
            String token = StpUtil.getTokenValueByLoginId(admin.getId());
            result.setLoginToken(token);
            result.setCheckLogin(token != null);
            resultList.add(result);
        }
        return ResponseUtil.okList(resultList , adminList);
    }

    /**
     * 详情
     */
    @SaCheckPermission("admin:admin:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(adminService.findById(id));
    }

    /**
     * 管理员强制退出登录
     */
    @SaCheckPermission("admin:admin:logout")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "强制退出登录")
    @PostMapping("/logout")
    public Object logout(@JsonBody Integer id) {
        List<String> tokenList = StpUtil.getTokenValueListByLoginId(id);
        for (String token :tokenList) {
            StpUtil.logoutByTokenValue(token);
        }
        return ResponseUtil.ok();
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowAdmin admin) {
        Object error = adminService.validate(admin);
        if (error != null) {
            return error;
        }
        String username = admin.getUsername();
        List<ShopflowAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0204);
        }
        if (!adminService.saveAdmin(admin)){
            return ResponseUtil.fail("管理员添加失败");
        }
        ActionLogHandler.logAuthSucceed("添加管理员", username);
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:admin:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowAdmin admin) {
        Object error = adminService.validate(admin);
        if (error != null) {
            return error;
        }
        if (admin.getId() == null) {
            return ResponseUtil.badArgument();
        }
        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(null);
        if (adminService.updateVersionSelective(admin) == 0){
            throw new RuntimeException("网络繁忙,请重试");
        }
        ActionLogHandler.logAuthSucceed("编辑管理员", admin.getUsername());
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:admin:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        // 管理员不能删除自身账号
        if (StpUtil.getLoginIdAsString().equals(id)) {
            return ResponseUtil.fail("管理员不能删除自身账号");
        }
        if (adminService.deleteById(id) == 0){
            return ResponseUtil.fail("管理员删除失败");
        }
        ActionLogHandler.logAuthSucceed("删除管理员", id);
        return ResponseUtil.ok();
    }


}
