package org.ysling.litemall.admin.web;
// Copyright (c) [ysling] [927069313@qq.com]
// [litemall-plus] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.

import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.model.share.body.ShareListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.admin.service.AdminShareService;

/**
 * 分享管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/share")
@Validated
public class AdminShareController {

    @Autowired
    private AdminShareService shareService;

    /**
     * 分享记录查询
     */
    @SaCheckPermission("admin:share:list")
    @RequiresPermissionsDesc(menu = {"分享管理", "分享记录"}, button = "查询")
    @GetMapping("/list")
    public Object list(ShareListBody body) {
        return ResponseUtil.okList(shareService.querySelective(body));
    }


}
