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
import org.ysling.shopflow.admin.model.keyword.body.KeywordListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowKeyword;
import org.ysling.shopflow.admin.service.AdminKeywordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;


/**
 * 关键词
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/keyword")
@Validated
public class AdminKeywordController {

    @Autowired
    private AdminKeywordService keywordService;

    /**
     * 查询
     */
    @SaCheckPermission("admin:keyword:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "关键词"}, button = "查询")
    @GetMapping("/list")
    public Object list(KeywordListBody body) {
        return ResponseUtil.okList(keywordService.querySelective(body));
    }

    /**
     * 详情
     */
    @SaCheckPermission("admin:keyword:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "关键词"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(keywordService.findById(id));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:keyword:create")
    @RequiresPermissionsDesc(menu = {"商场管理", "关键词"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowKeyword keyword) {
        Object error = keywordService.validate(keyword);
        if (error != null) {
            return error;
        }
        if (keywordService.add(keyword) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:keyword:update")
    @RequiresPermissionsDesc(menu = {"商场管理", "关键词"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowKeyword keyword) {
        Object error = keywordService.validate(keyword);
        if (error != null) {
            return error;
        }
        if (keywordService.updateVersionSelective(keyword) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:keyword:delete")
    @RequiresPermissionsDesc(menu = {"商场管理", "关键词"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (keywordService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }

}
