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
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.system.body.SystemListBody;
import org.ysling.shopflow.admin.service.AdminSystemService;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.utils.JacksonUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.db.domain.ShopflowSystem;

import javax.validation.Valid;
import java.util.Map;

/**
 * 系统配置
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/config")
@Validated
public class AdminConfigController {

    @Autowired
    private AdminSystemService systemService;

    /**
     * 查询
     */
    @SaCheckPermission("admin:config:list")
    @RequiresPermissionsDesc(menu = {"配置管理", "全部配置"}, button = "查询全部")
    @GetMapping("/list")
    public Object list(SystemListBody body) {
        return ResponseUtil.okList(systemService.querySelective(body));
    }

    /**
     * 配置编辑
     */
    @SaCheckPermission("admin:config:update")
    @RequiresPermissionsDesc(menu = {"配置管理", "全部配置"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowSystem system) {
        Object error = systemService.validate(system);
        if (error != null) {
            return error;
        }
        if (systemService.updateVersionSelective(system) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        SystemUtils.updateConfig(system);
        return ResponseUtil.ok();
    }


}
