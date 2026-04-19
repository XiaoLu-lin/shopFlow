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
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.core.service.StatCoreService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.litemall.db.entity.StatResult;
import java.util.List;
import java.util.Map;

/**
 * 统计管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/stat")
@Validated
public class AdminStatController {

    @Autowired
    private StatCoreService statService;

    /**
     * 用户统计
     */
    @SaCheckPermission("admin:stat:user")
    @RequiresPermissionsDesc(menu = {"统计管理", "用户统计"}, button = "查询")
    @GetMapping("/user")
    public Object statUser() {
        List<Map<String, Object>> rows = statService.statUser();
        String[] columns = new String[]{"day", "users"};
        StatResult result = new StatResult();
        result.setColumns(columns);
        result.setRows(rows);
        return ResponseUtil.ok(result);
    }

    /**
     * 订单统计
     */
    @SaCheckPermission("admin:stat:order")
    @RequiresPermissionsDesc(menu = {"统计管理", "订单统计"}, button = "查询")
    @GetMapping("/order")
    public Object statOrder() {
        List<Map<String, Object>> rows = statService.statOrder();
        String[] columns = new String[]{"day", "orders", "customers", "amount", "pcr"};
        StatResult result = new StatResult();
        result.setColumns(columns);
        result.setRows(rows);
        return ResponseUtil.ok(result);
    }

    /**
     * 商品统计
     */
    @SaCheckPermission("admin:stat:goods")
    @RequiresPermissionsDesc(menu = {"统计管理", "商品统计"}, button = "查询")
    @GetMapping("/goods")
    public Object statGoods() {
        List<Map<String, Object>> rows = statService.statGoods();
        String[] columns = new String[]{"day", "orders", "products", "amount"};
        StatResult result = new StatResult();
        result.setColumns(columns);
        result.setRows(rows);
        return ResponseUtil.ok(result);
    }

}
