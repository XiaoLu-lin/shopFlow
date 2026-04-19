package org.ysling.litemall.admin.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.notice.admin.body.NoticeAdminListBody;
import org.ysling.litemall.db.domain.LitemallNoticeAdmin;
import org.ysling.litemall.db.service.impl.NoticeAdminServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_noticeAdmin")
public class AdminNoticeAdminService extends NoticeAdminServiceImpl {

    
    @Cacheable(sync = true)
    public List<LitemallNoticeAdmin> querySelective(NoticeAdminListBody body) {
        QueryWrapper<LitemallNoticeAdmin> wrapper = startPage(body);
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , body.getAdminId());
        if("read".equals(body.getAdminId())){
            wrapper.isNotNull(LitemallNoticeAdmin.READ_TIME);
        }
        if("unread".equals(body.getAdminId())){
            wrapper.isNull(LitemallNoticeAdmin.READ_TIME);
        }
        if(StringUtils.hasText(body.getTitle())){
            wrapper.like(LitemallNoticeAdmin.NOTICE_TITLE , body.getTitle());
        }
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public LitemallNoticeAdmin find(String noticeId, String adminId) {
        QueryWrapper<LitemallNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.NOTICE_ID , noticeId);
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , adminId);
        return getOne(wrapper);
    }


    @CacheEvict(allEntries = true)
    public void markReadByIds(List<String> ids, String adminId) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(LitemallNoticeAdmin.ID , ids);
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , adminId);
        LitemallNoticeAdmin noticeAdmin = new LitemallNoticeAdmin();
        LocalDateTime now = LocalDateTime.now();
        noticeAdmin.setReadTime(now);
        noticeAdmin.setUpdateTime(now);
        update(noticeAdmin, wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteById(String id, String adminId) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.ID , id);
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , adminId);
        LitemallNoticeAdmin noticeAdmin = new LitemallNoticeAdmin();
        noticeAdmin.setUpdateTime(LocalDateTime.now());
        noticeAdmin.setDeleted(true);
        update(noticeAdmin, wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByIds(List<String> ids, String adminId) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(LitemallNoticeAdmin.ID , ids);
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , adminId);
        remove(wrapper);
    }

    
    @Cacheable(sync = true)
    public Integer countUnread(String adminId) {
        QueryWrapper<LitemallNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.ADMIN_ID , adminId);
        wrapper.isNull(LitemallNoticeAdmin.READ_TIME);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<LitemallNoticeAdmin> queryByNoticeId(String noticeId) {
        QueryWrapper<LitemallNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.NOTICE_ID , noticeId);
        return queryAll(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByNoticeId(String id) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.NOTICE_ID , id);
        remove(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByNoticeIds(List<String> ids) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(LitemallNoticeAdmin.NOTICE_ID , ids);
        remove(wrapper);
    }

    
    @Cacheable(sync = true)
    public Integer countReadByNoticeId(String noticeId) {
        QueryWrapper<LitemallNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.NOTICE_ID , noticeId);
        wrapper.isNotNull(LitemallNoticeAdmin.READ_TIME);
        return Math.toIntExact(count(wrapper));
    }

    
    @CacheEvict(allEntries = true)
    public void updateByNoticeId(LitemallNoticeAdmin noticeAdmin, String noticeId) {
        UpdateWrapper<LitemallNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(LitemallNoticeAdmin.NOTICE_ID , noticeId);
        update(noticeAdmin , wrapper);
    }


}
