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
import org.ysling.litemall.admin.model.groupon.body.GrouponListBody;
import org.ysling.litemall.admin.model.groupon.body.GrouponRuleListBody;
import org.ysling.litemall.core.tasks.impl.GrouponRuleExpiredTask;
import org.ysling.litemall.core.tasks.service.TaskService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.domain.LitemallGrouponRules;
import org.ysling.litemall.db.enums.GrouponRuleStatus;
import org.ysling.litemall.admin.service.AdminGoodsService;
import org.ysling.litemall.admin.service.AdminGrouponRulesService;
import org.ysling.litemall.admin.service.AdminGrouponService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


/**
 * 团购管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/groupon")
@Validated
public class AdminGrouponController {

    @Autowired
    private AdminGrouponRulesService rulesService;
    @Autowired
    private AdminGoodsService goodsService;
    @Autowired
    private AdminGrouponService grouponService;
    @Autowired
    private TaskService taskService;


    /**
     * 查询参与用户
     */
    @SaCheckPermission("admin:groupon:join")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "查询参与用户")
    @GetMapping("/join")
    public Object join(GrouponListBody body) {
        return ResponseUtil.okList(grouponService.querySelective(body));
    }

    /**
     * 团购规则详情
     * @param id  团购规则ID
     */
    @SaCheckPermission("admin:groupon:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(rulesService.findById(id));
    }

    /**
     * 团购规则列表
     */
    @SaCheckPermission("admin:groupon:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(GrouponRuleListBody body) {
        return ResponseUtil.okList(rulesService.querySelective(body));
    }

    /**
     * 团购规则编辑
     */
    @SaCheckPermission("admin:groupon:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody LitemallGrouponRules grouponRules) {
        Object error = rulesService.validate(grouponRules);
        if (error != null) {
            return error;
        }

        LitemallGrouponRules rules = rulesService.findById(grouponRules.getId());
        if(rules == null){
            return ResponseUtil.badArgumentValue();
        }
        if(!rules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_ON.getStatus())){
            return ResponseUtil.fail( "团购已经下线");
        }

        String goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());
        if (rulesService.updateVersionSelective(grouponRules) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 团购规则添加
     */
    @SaCheckPermission("admin:groupon:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody LitemallGrouponRules grouponRules) {
        Object error = rulesService.validate(grouponRules);
        if (error != null) {
            return error;
        }

        String goodsId = grouponRules.getGoodsId();
        LitemallGoods goods = goodsService.findById(goodsId);
        if (goods == null) {
            return ResponseUtil.fail( "团购商品不存在");
        }
        if(rulesService.countByGoodsId(goodsId) > 0){
            return ResponseUtil.fail( "团购商品已经存在");
        }

        //添加团购规则信息
        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());
        grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        if (rulesService.add(grouponRules) == 0){
            return ResponseUtil.addDataFailed();
        }

        //添加商品他团购信息
        goods.setIsGroupon(true);
        if (goodsService.updateVersionSelective(goods) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = grouponRules.getExpireTime();
        long delay = ChronoUnit.MILLIS.between(now, expire);
        // 团购过期任务
        taskService.addTask(new GrouponRuleExpiredTask(grouponRules, delay));
        return ResponseUtil.ok();
    }

    /**
     * 团购规则删除
     * @param id 团购规则ID
     */
    @SaCheckPermission("admin:groupon:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "团购管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        LitemallGrouponRules grouponRules = new LitemallGrouponRules();
        grouponRules.setId(id);
        //删除团购超时任务
        taskService.removeTask(new GrouponRuleExpiredTask(grouponRules));
        //设置团购立马过期
        GrouponRuleExpiredTask expiredTask = new GrouponRuleExpiredTask(grouponRules);
        expiredTask.run();
        if (rulesService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
