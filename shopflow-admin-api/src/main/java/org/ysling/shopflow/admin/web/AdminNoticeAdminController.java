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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.admin.service.AdminAdminService;
import org.ysling.shopflow.admin.service.AdminNoticeAdminService;
import org.ysling.shopflow.admin.service.AdminNoticeService;
import org.ysling.shopflow.admin.model.notice.admin.body.NoticeAdminListBody;
import org.ysling.shopflow.admin.model.notice.admin.result.NoticeAdminCatResult;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.domain.ShopflowNotice;
import org.ysling.shopflow.db.domain.ShopflowNoticeAdmin;
import org.ysling.shopflow.db.entity.IdsBody;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.time.LocalDateTime;

/**
 * 管理员通知信息
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/profile")
@Validated
public class AdminNoticeAdminController {

    @Autowired
    private AdminAdminService adminService;
    @Autowired
    private AdminNoticeService noticeService;
    @Autowired
    private AdminNoticeAdminService noticeAdminService;

    /**
     * 通知列表
     */
    @SaCheckLogin
    @GetMapping("/notice/list")
    public Object listNotice(NoticeAdminListBody body) {
        body.setAdminId(StpUtil.getLoginIdAsString());
        return ResponseUtil.okList(noticeAdminService.querySelective(body));
    }

    /**
     * 通知信息条数
     */
    @SaCheckLogin
    @GetMapping("/notice/count")
    public Object countNotice() {
        return ResponseUtil.ok(noticeAdminService.countUnread(StpUtil.getLoginIdAsString()));
    }

    /**
     * 查看通知
     */
    @SaCheckLogin
    @PostMapping("/notice/cat")
    public Object catNotice(@JsonBody String noticeId) {
        ShopflowNoticeAdmin noticeAdmin = noticeAdminService.find(noticeId, StpUtil.getLoginIdAsString());
        if(noticeAdmin == null){
           return ResponseUtil.badArgumentValue();
        }

        // 更新通知记录中的时间
        noticeAdmin.setReadTime(LocalDateTime.now());
        if (noticeAdminService.updateVersionSelective(noticeAdmin) == 0){
            return ResponseUtil.updatedDateExpired();
        }
        ShopflowNotice notice = noticeService.findById(noticeId);
        NoticeAdminCatResult result = new NoticeAdminCatResult();
        result.setTitle(notice.getTitle());
        result.setContent(notice.getContent());
        result.setTime(notice.getAddTime());
        if(notice.getAdminId().equals("0")){
            result.setAdmin("系统");
        } else{
            ShopflowAdmin admin = adminService.findById(notice.getAdminId());
            result.setAdmin(admin.getUsername());
            result.setAvatar(admin.getAvatar());
        }
        return ResponseUtil.ok(result);
    }

    /**
     * 批量浏览通知
     */
    @SaCheckLogin
    @PostMapping("/notice/batch-cat")
    public Object allCatNotice(@Valid @RequestBody IdsBody body) {
        noticeAdminService.markReadByIds(body.getIds(), StpUtil.getLoginIdAsString());
        return ResponseUtil.ok();
    }

    /**
     * 删除通知
     * @param id 通知ID
     */
    @SaCheckLogin
    @PostMapping("/notice/delete")
    public Object deleteNotice(@JsonBody String id) {
        noticeAdminService.deleteById(id, StpUtil.getLoginIdAsString());
        return ResponseUtil.ok();
    }

    /**
     * 批量删除通知
     */
    @SaCheckLogin
    @PostMapping("/notice/batch-delete")
    public Object allDeleteNotice(@Valid @RequestBody IdsBody body) {
        noticeAdminService.deleteByIds(body.getIds(), StpUtil.getLoginIdAsString());
        return ResponseUtil.ok();
    }

}
