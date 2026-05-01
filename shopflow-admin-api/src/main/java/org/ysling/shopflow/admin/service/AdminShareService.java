package org.ysling.shopflow.admin.service;
// Copyright (c) [ysling] [927069313@qq.com]
// [ShopFlow] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.admin.model.share.body.ShareListBody;
import org.ysling.shopflow.db.domain.ShopflowShare;
import org.ysling.shopflow.db.service.impl.ShareServiceImpl;
import java.util.List;


/**
 * 分享记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_share")
public class AdminShareService extends ShareServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowShare> querySelective(ShareListBody body) {
        QueryWrapper<ShopflowShare> wrapper = startPage(body);
        if (body.getInviterId() != null){
            wrapper.eq(ShopflowShare.INVITER_ID , body.getInviterId());
        }
        if (body.getUserId() != null){
            wrapper.eq(ShopflowShare.USER_ID , body.getUserId());
        }
        if (body.getStatus() != null){
            wrapper.eq(ShopflowShare.STATUS , body.getStatus());
        }
        return queryAll(wrapper);
    }


}
