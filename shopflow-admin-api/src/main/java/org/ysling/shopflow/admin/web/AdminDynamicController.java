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

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.dynamic.body.DynamicListBody;
import org.ysling.shopflow.admin.model.dynamic.result.DynamicListResult;
import org.ysling.shopflow.core.jobs.ApiJob;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowDynamic;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.admin.service.AdminDynamicService;
import org.ysling.shopflow.admin.service.AdminUserService;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 动态信息发布
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/dynamic")
@Validated
public class AdminDynamicController {

    @Autowired
    private AdminUserService userService;
    @Autowired
    private AdminDynamicService dynamicService;


    /**
     * 动态列表
     */
    @SaCheckPermission("admin:dynamic:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "动态管理"}, button = "查询")
    @GetMapping("/list")
    public Object dynamicList(DynamicListBody body) {
        List<ShopflowDynamic> dynamicList = dynamicService.querySelective(body);
        ArrayList<DynamicListResult> resultList = new ArrayList<>();
        for (ShopflowDynamic dynamic : dynamicList) {
            DynamicListResult result = new DynamicListResult();
            BeanUtil.copyProperties(dynamic , result);
            //给查寻出来的时间加上浏览量
            ShopflowUser user = userService.findById(dynamic.getUserId());
            if (user == null){
                if (ApiJob.USER_ID.equals(dynamic.getUserId())){
                    result.setNickName("每日段子");
                    result.setAvatarUrl("https://th.bing.com/th?id=OSK.2fe5b3f3f141834f896fe8a9ffe3a1dc&w=148&h=148&c=7&o=6&dpr=1.8&pid=SANGAM");
                } else {
                    continue;
                }
            } else {
                result.setNickName(user.getNickName());
                result.setAvatarUrl(user.getAvatarUrl());
            }
            resultList.add(result);
        }
        return ResponseUtil.okList(resultList, dynamicList);
    }


    /**
     * 修改日常
     */
    @SaCheckPermission("admin:dynamic:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "动态管理"}, button = "修改")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody ShopflowDynamic dynamic) {
        String content = dynamic.getContent();
        if (Objects.isNull(content)) {
            return ResponseUtil.badArgument();
        }
        if (dynamicService.updateSelective(dynamic) == 0){
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 发布日常
     */
    @SaCheckPermission("admin:dynamic:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "动态管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody ShopflowDynamic dynamic) {
        String content = dynamic.getContent();
        if (Objects.isNull(content)) {
            return ResponseUtil.badArgument();
        }
        if (dynamicService.add(dynamic) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除日常
     * @param id 动态发布信息的id
     */
    @SaCheckPermission("admin:dynamic:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "动态管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (dynamicService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
