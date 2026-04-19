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
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import lombok.extern.slf4j.Slf4j;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/index")
public class AdminIndexController {

    @RequestMapping("/index")
    public Object index() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @SaCheckRole
    @RequestMapping("/guest")
    public Object guest() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @SaCheckPermission("admin:brand:list")
    @RequestMapping("/authn")
    public Object authn() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @SaCheckRole
    @RequestMapping("/user")
    public Object user() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @SaCheckRole("admin")
    @RequestMapping("/admin")
    public Object admin() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

    @SaCheckRole("admin2")
    @RequestMapping("/admin2")
    public Object admin2() {
        return ResponseUtil.ok("hello world, this is admin service");
    }

}
