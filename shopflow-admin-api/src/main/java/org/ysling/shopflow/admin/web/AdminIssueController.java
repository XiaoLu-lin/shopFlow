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
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.issue.body.IssueListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowIssue;
import org.ysling.shopflow.admin.service.AdminIssueService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;

/**
 * 通用问题
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/issue")
@Validated
public class AdminIssueController {

    @Autowired
    private AdminIssueService issueService;

    /**
     * 列表
     */
    @SaCheckPermission("admin:issue:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "通用问题"}, button = "查询")
    @GetMapping("/list")
    public Object list(IssueListBody body) {
        return ResponseUtil.okList(issueService.querySelective(body));
    }

    /**
     * 详情
     */
    @SaCheckPermission("admin:issue:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "通用问题"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(issueService.findById(id));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:issue:create")
    @RequiresPermissionsDesc(menu = {"商场管理", "通用问题"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowIssue issue) {
        Object error = issueService.validate(issue);
        if (error != null) {
            return error;
        }
        if (issueService.add(issue) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:issue:update")
    @RequiresPermissionsDesc(menu = {"商场管理", "通用问题"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowIssue issue) {
        Object error = issueService.validate(issue);
        if (error != null) {
            return error;
        }
        if (issueService.updateVersionSelective(issue) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:issue:delete")
    @RequiresPermissionsDesc(menu = {"商场管理", "通用问题"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (issueService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }

}
