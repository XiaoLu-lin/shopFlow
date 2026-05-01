package org.ysling.shopflow.admin.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.notice.admin.body.NoticeAdminListBody;
import org.ysling.shopflow.db.domain.ShopflowNoticeAdmin;
import org.ysling.shopflow.db.service.impl.NoticeAdminServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_noticeAdmin")
public class AdminNoticeAdminService extends NoticeAdminServiceImpl {

    
    @Cacheable(sync = true)
    public List<ShopflowNoticeAdmin> querySelective(NoticeAdminListBody body) {
        QueryWrapper<ShopflowNoticeAdmin> wrapper = startPage(body);
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , body.getAdminId());
        if("read".equals(body.getAdminId())){
            wrapper.isNotNull(ShopflowNoticeAdmin.READ_TIME);
        }
        if("unread".equals(body.getAdminId())){
            wrapper.isNull(ShopflowNoticeAdmin.READ_TIME);
        }
        if(StringUtils.hasText(body.getTitle())){
            wrapper.like(ShopflowNoticeAdmin.NOTICE_TITLE , body.getTitle());
        }
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public ShopflowNoticeAdmin find(String noticeId, String adminId) {
        QueryWrapper<ShopflowNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.NOTICE_ID , noticeId);
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , adminId);
        return getOne(wrapper);
    }


    @CacheEvict(allEntries = true)
    public void markReadByIds(List<String> ids, String adminId) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(ShopflowNoticeAdmin.ID , ids);
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , adminId);
        ShopflowNoticeAdmin noticeAdmin = new ShopflowNoticeAdmin();
        LocalDateTime now = LocalDateTime.now();
        noticeAdmin.setReadTime(now);
        noticeAdmin.setUpdateTime(now);
        update(noticeAdmin, wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteById(String id, String adminId) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.ID , id);
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , adminId);
        ShopflowNoticeAdmin noticeAdmin = new ShopflowNoticeAdmin();
        noticeAdmin.setUpdateTime(LocalDateTime.now());
        noticeAdmin.setDeleted(true);
        update(noticeAdmin, wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByIds(List<String> ids, String adminId) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(ShopflowNoticeAdmin.ID , ids);
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , adminId);
        remove(wrapper);
    }

    
    @Cacheable(sync = true)
    public Integer countUnread(String adminId) {
        QueryWrapper<ShopflowNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.ADMIN_ID , adminId);
        wrapper.isNull(ShopflowNoticeAdmin.READ_TIME);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<ShopflowNoticeAdmin> queryByNoticeId(String noticeId) {
        QueryWrapper<ShopflowNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.NOTICE_ID , noticeId);
        return queryAll(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByNoticeId(String id) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.NOTICE_ID , id);
        remove(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByNoticeIds(List<String> ids) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.in(ShopflowNoticeAdmin.NOTICE_ID , ids);
        remove(wrapper);
    }

    
    @Cacheable(sync = true)
    public Integer countReadByNoticeId(String noticeId) {
        QueryWrapper<ShopflowNoticeAdmin> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.NOTICE_ID , noticeId);
        wrapper.isNotNull(ShopflowNoticeAdmin.READ_TIME);
        return Math.toIntExact(count(wrapper));
    }

    
    @CacheEvict(allEntries = true)
    public void updateByNoticeId(ShopflowNoticeAdmin noticeAdmin, String noticeId) {
        UpdateWrapper<ShopflowNoticeAdmin> wrapper = new UpdateWrapper<>();
        wrapper.eq(ShopflowNoticeAdmin.NOTICE_ID , noticeId);
        update(noticeAdmin , wrapper);
    }


}
