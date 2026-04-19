package org.ysling.litemall.wx.service;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.db.domain.LitemallTopic;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.service.impl.TopicServiceImpl;
import org.ysling.litemall.wx.model.topic.body.TopicListBody;
import java.util.List;

/**
 * 专题服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_topic")
public class WxTopicService extends TopicServiceImpl {


    
    @Cacheable(sync = true)
    public List<LitemallTopic> queryList(PageBody body) {
        return queryAll(startPage(body));
    }

    
    @Cacheable(sync = true)
    public List<LitemallTopic> queryList(TopicListBody body) {
        QueryWrapper<LitemallTopic> wrapper = startPage(body);
        if (StringUtils.hasText(body.getTitle())){
            wrapper.like(LitemallTopic.TITLE , body.getTitle());
        }
        wrapper.orderByDesc(LitemallTopic.WEIGHT);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallTopic> queryRelatedList(String id, Integer limit) {
        QueryWrapper<LitemallTopic> wrapper = startPage(new PageBody(limit));
        wrapper.notIn(LitemallTopic.ID , id);
        wrapper.orderByDesc(LitemallTopic.WEIGHT);
        return queryAll(wrapper);
    }


}
