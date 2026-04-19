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
import org.ysling.litemall.admin.model.keyword.body.KeywordListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallKeyword;
import org.ysling.litemall.db.domain.LitemallTopic;
import org.ysling.litemall.db.service.impl.KeywordServiceImpl;
import java.util.List;
import java.util.Objects;

/**
 * 关键字服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_keyword")
public class AdminKeywordService extends KeywordServiceImpl {


    public Object validate(LitemallKeyword keywords) {
        String keyword = keywords.getKeyword();
        if (Objects.isNull(keyword)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
    
    @Cacheable(sync = true)
    public List<LitemallKeyword> querySelective(KeywordListBody body) {
        QueryWrapper<LitemallKeyword> wrapper = startPage(body);
        if (StringUtils.hasText(body.getKeyword())) {
            wrapper.like(LitemallKeyword.KEYWORD , body.getKeyword());
        }
        if (StringUtils.hasText(body.getUrl())) {
            wrapper.like(LitemallKeyword.URL , body.getUrl());
        }
        wrapper.orderByDesc(LitemallKeyword.WEIGHT);
        return queryAll(wrapper);
    }

}
