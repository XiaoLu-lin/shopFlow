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
import org.ysling.litemall.admin.model.aftersale.body.AftersaleListBody;
import org.ysling.litemall.core.service.AftersaleCoreService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.entity.IdsBody;
import org.ysling.litemall.admin.service.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;

/**
 * 订单售后
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/aftersale")
@Validated
public class AdminAftersaleController {


    @Autowired
    private AdminAftersaleService aftersaleService;
    @Autowired
    private AftersaleCoreService aftersaleCoreService;



    /**
     * 查询
     */
    @SaCheckPermission("admin:aftersale:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(AftersaleListBody body) {
        return ResponseUtil.okList(aftersaleService.querySelective(body));
    }


    /**
     * 审核通过
     */
    @SaCheckPermission("admin:aftersale:recept")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "审核通过")
    @PostMapping("/recept")
    public Object recept(@JsonBody String id) {
        LitemallAftersale aftersale = aftersaleService.findById(id);
        if(aftersale == null){
            return ResponseUtil.fail("售后不存在");
        }
        return aftersaleCoreService.recept(aftersale);
    }

    /**
     * 批量通过
     */
    @SaCheckPermission("admin:aftersale:batch-recept")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "批量通过")
    @PostMapping("/batch-recept")
    public Object batchRecept(@Valid @RequestBody IdsBody body) {
        for(String id : body.getIds()) {
            LitemallAftersale aftersale = aftersaleService.findById(id);
            if(aftersale == null){
                continue;
            }
            aftersaleCoreService.recept(aftersale);
        }
        return ResponseUtil.ok();
    }


    /**
     * 审核拒绝
     */
    @SaCheckPermission("admin:aftersale:reject")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "审核拒绝")
    @PostMapping("/reject")
    public Object reject(@JsonBody String id) {
        LitemallAftersale aftersale = aftersaleService.findById(id);
        if(aftersale == null){
            return ResponseUtil.badArgumentValue();
        }
        return aftersaleCoreService.reject(aftersale);
    }


    /**
     * 批量拒绝
     */
    @SaCheckPermission("admin:aftersale:batch-reject")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "批量拒绝")
    @PostMapping("/batch-reject")
    public Object batchReject(@Valid @RequestBody IdsBody body) {
        for(String id : body.getIds()) {
            LitemallAftersale aftersale = aftersaleService.findById(id);
            if(aftersale == null){
                continue;
            }
            aftersaleCoreService.reject(aftersale);
        }
        return ResponseUtil.ok();
    }


    /**
     * 退款
     */
    @SaCheckPermission("admin:aftersale:refund")
    @RequiresPermissionsDesc(menu = {"商场管理", "售后管理"}, button = "退款")
    @PostMapping("/refund")
    public Object refund(@JsonBody String id) {
        return aftersaleCoreService.refund(id);
    }


}
