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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.topic.body.TopicListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallAd;
import org.ysling.litemall.db.domain.LitemallTopic;
import org.ysling.litemall.db.service.impl.TopicServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 专题服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_topic")
public class AdminTopicService extends TopicServiceImpl {

    public Object validate(LitemallTopic topic) {
        String title = topic.getTitle();
        if (Objects.isNull(title)) {
            return ResponseUtil.fail("标题不能为空");
        }
        String content = topic.getContent();
        if (Objects.isNull(content)) {
            return ResponseUtil.fail("内容不能为空");
        }
        String author = topic.getAuthor();
        if (author == null){
            return ResponseUtil.fail("作者不能为空");
        }
        String picUrl = topic.getPicUrl();
        if (picUrl == null){
            return ResponseUtil.fail("图片不能为空");
        }
        BigDecimal price = topic.getPrice();
        if (Objects.isNull(price)) {
            return ResponseUtil.fail("商品底价不能为空");
        }
        String readCount = topic.getReadCount();
        if (readCount == null){
            return ResponseUtil.fail("浏览量不能为空");
        }
        return null;
    }

    @Cacheable(sync = true)
    public List<LitemallTopic> querySelective(TopicListBody body) {
        QueryWrapper<LitemallTopic> wrapper = startPage(body);
        if (StringUtils.hasText(body.getTitle())) {
            wrapper.like(LitemallTopic.TITLE , body.getTitle());
        }
        if (StringUtils.hasText(body.getSubtitle())) {
            wrapper.like(LitemallTopic.SUBTITLE , body.getSubtitle());
        }
        wrapper.orderByDesc(LitemallTopic.WEIGHT);
        return queryAll(wrapper);
    }

}
