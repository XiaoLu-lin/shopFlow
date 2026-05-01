package org.ysling.shopflow.admin.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.democrud.body.DemoCrudListBody;
import org.ysling.shopflow.admin.service.AdminDemoCrudService;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowIssue;

import javax.validation.Valid;

/**
 * CRUD 练习
 */
@Slf4j
@RestController
@RequestMapping("/admin/demo-crud")
@Validated
public class AdminDemoCrudController {

    @Autowired
    private AdminDemoCrudService demoCrudService;

    /**
     * 列表
     */
    @SaCheckPermission("admin:issue:list")
    @RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "查询")
    @GetMapping("/list")
    public Object list(DemoCrudListBody body) {
        return ResponseUtil.okList(demoCrudService.querySelective(body));
    }

    /**
     * 详情
     */
    @SaCheckPermission("admin:issue:read")
    @RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(demoCrudService.findById(id));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:issue:create")
    @RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowIssue issue) {
        Object error = demoCrudService.validate(issue);
        if (error != null) {
            return error;
        }
        if (demoCrudService.add(issue) == 0) {
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:issue:update")
    @RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowIssue issue) {
        Object error = demoCrudService.validate(issue);
        if (error != null) {
            return error;
        }
        if (demoCrudService.updateVersionSelective(issue) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:issue:delete")
    @RequiresPermissionsDesc(menu = {"商城管理", "CRUD练习"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (demoCrudService.deleteById(id) == 0) {
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }
}
