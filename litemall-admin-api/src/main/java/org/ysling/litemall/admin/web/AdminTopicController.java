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
import org.ysling.litemall.admin.model.topic.body.TopicListBody;
import org.ysling.litemall.admin.model.topic.result.TopicReadResult;
import org.ysling.litemall.core.service.StorageCoreService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallTopic;
import org.ysling.litemall.admin.service.AdminGoodsService;
import org.ysling.litemall.admin.service.AdminTopicService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.db.entity.IdsBody;

import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;

/**
 * 专题管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/topic")
@Validated
public class AdminTopicController {

    @Autowired
    private StorageCoreService storageCoreService;
    @Autowired
    private AdminTopicService topicService;
    @Autowired
    private AdminGoodsService goodsService;

    /**
     * 专题列表
     */
    @SaCheckPermission("admin:topic:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(TopicListBody body) {
        return ResponseUtil.okList(topicService.querySelective(body));
    }

    /**
     * 专题添加
     */
    @SaCheckPermission("admin:topic:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody LitemallTopic topic) {
        Object error = topicService.validate(topic);
        if (error != null) {
            return error;
        }
        String content = topic.getContent();
        topic.setContent(storageCoreService.uploadTextFile(content));
        if (topicService.add(topic) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 专题详情
     * @param id 专题ID
     */
    @SaCheckPermission("admin:topic:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        LitemallTopic topic = topicService.findById(id);
        TopicReadResult result = new TopicReadResult();
        result.setTopic(topic);
        result.setGoodsList(goodsService.queryByIds(topic.getGoodsIds()));
        return ResponseUtil.ok(result);
    }

    /**
     * 专题编辑
     */
    @SaCheckPermission("admin:topic:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody LitemallTopic topic) {
        Object error = topicService.validate(topic);
        if (error != null) {
            return error;
        }
        String content = topic.getContent();
        String textFile = storageCoreService.uploadTextFile(content);
        topic.setContent(textFile);
        if (topicService.updateVersionSelective(topic) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 专题删除
     */
    @SaCheckPermission("admin:topic:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (topicService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 专题批量删除
     */
    @SaCheckPermission("admin:topic:batch-delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "批量删除")
    @PostMapping("/batch-delete")
    public Object batchDelete(@Valid @RequestBody IdsBody body) {
        if (!topicService.removeByIds(body.getIds())){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
