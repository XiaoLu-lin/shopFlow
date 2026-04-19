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
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.model.notice.body.NoticeListBody;
import org.ysling.litemall.admin.model.notice.result.NoticeReadResult;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallAdmin;
import org.ysling.litemall.db.domain.LitemallNotice;
import org.ysling.litemall.db.domain.LitemallNoticeAdmin;
import org.ysling.litemall.admin.service.AdminAdminService;
import org.ysling.litemall.admin.service.AdminNoticeAdminService;
import org.ysling.litemall.admin.service.AdminNoticeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.db.entity.IdsBody;

import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;


/**
 * 通知管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/notice")
@Validated
public class AdminNoticeController {

    @Autowired
    private AdminNoticeService noticeService;
    @Autowired
    private AdminAdminService adminService;
    @Autowired
    private AdminNoticeAdminService noticeAdminService;

    /**
     * 查询
     */
    @SaCheckPermission("admin:notice:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "通知管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(NoticeListBody body) {
        return ResponseUtil.okList(noticeService.querySelective(body));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:notice:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "通知管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody LitemallNotice notice) {
        Object error = noticeService.validate(notice);
        if (error != null) {
            return error;
        }
        // 1. 添加通知记录
        notice.setAdminId(StpUtil.getLoginIdAsString());
        if (noticeService.add(notice) == 0){
            return ResponseUtil.addDataFailed();
        }
        // 2. 添加管理员通知记录
        List<LitemallAdmin> adminList = adminService.list();
        LitemallNoticeAdmin noticeAdmin = new LitemallNoticeAdmin();
        noticeAdmin.setNoticeId(notice.getId());
        noticeAdmin.setNoticeTitle(notice.getTitle());
        noticeAdmin.setNoticeContent(notice.getContent());
        ArrayList<LitemallNoticeAdmin> noticeAdmins = new ArrayList<>();
        for(LitemallAdmin admin : adminList){
            noticeAdmin.setAdminId(admin.getId());
            noticeAdmins.add(noticeAdmin);
        }
        if (noticeAdminService.batchAdd(noticeAdmins)){
            throw new RuntimeException("通知添加失败请重试");
        }
        return ResponseUtil.ok();
    }

    /**
     * 详情
     * @param id 通知ID
     */
    @SaCheckPermission("admin:notice:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "通知管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        NoticeReadResult result = new NoticeReadResult();
        result.setNotice(noticeService.findById(id));
        result.setNoticeAdminList(noticeAdminService.queryByNoticeId(id));
        return ResponseUtil.ok(result);
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:notice:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "通知管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody LitemallNotice notice) {
        Object error = noticeService.validate(notice);
        if (error != null) {
            return error;
        }
        LitemallNotice originalNotice = noticeService.findById(notice.getId());
        if (originalNotice == null) {
            return ResponseUtil.badArgument();
        }
        // 如果通知已经有人阅读过，则不支持编辑
        if(noticeAdminService.countReadByNoticeId(notice.getId()) > 0){
            return ResponseUtil.fail( "通知已被阅读，不能重新编辑");
        }
        // 1. 更新通知记录
        notice.setAdminId(StpUtil.getLoginIdAsString());
        if (noticeService.updateVersionSelective(notice) == 0){
            return ResponseUtil.updatedDateExpired();
        }
        // 2. 更新管理员通知记录
        if(!originalNotice.getTitle().equals(notice.getTitle())){
            LitemallNoticeAdmin noticeAdmin = new LitemallNoticeAdmin();
            noticeAdmin.setNoticeTitle(notice.getTitle());
            noticeAdmin.setNoticeContent(notice.getContent());
            noticeAdminService.updateByNoticeId(noticeAdmin, notice.getId());
        }
        return ResponseUtil.ok(notice);
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:notice:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "通知管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        // 2. 删除通知记录
        noticeService.deleteById(id);
        // 1. 删除通知管理员记录
        noticeAdminService.deleteByNoticeId(id);
        return ResponseUtil.ok();
    }

    /**
     * 批量删除
     */
    @SaCheckPermission("admin:notice:batch-delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "通知管理"}, button = "批量删除")
    @PostMapping("/batch-delete")
    public Object batchDelete(@Valid @RequestBody IdsBody body) {
         // 2. 删除通知记录
         noticeService.removeByIds(body.getIds());
         // 1. 删除通知管理员记录
         noticeAdminService.deleteByNoticeIds(body.getIds());
         return ResponseUtil.ok();
    }


}
