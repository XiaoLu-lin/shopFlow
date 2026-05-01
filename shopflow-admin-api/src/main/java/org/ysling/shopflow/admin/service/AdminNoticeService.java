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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.notice.body.NoticeListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowNotice;
import org.ysling.shopflow.db.service.impl.NoticeServiceImpl;
import java.util.List;
import java.util.Objects;

/**
 * 通知服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_notice")
public class AdminNoticeService extends NoticeServiceImpl {



    public Object validate(ShopflowNotice notice) {
        String title = notice.getTitle();
        if (Objects.isNull(title)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @Cacheable(sync = true)
    public List<ShopflowNotice> querySelective(NoticeListBody body) {
        QueryWrapper<ShopflowNotice> wrapper = startPage(body);
        if (StringUtils.hasText(body.getTitle())) {
            wrapper.like(ShopflowNotice.TITLE , body.getTitle());
        }
        if (StringUtils.hasText(body.getContent())) {
            wrapper.like(ShopflowNotice.CONTENT , body.getContent());
        }
        return queryAll(wrapper);
    }


}
