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
import org.ysling.litemall.db.domain.LitemallKeyword;
import org.ysling.litemall.db.service.impl.KeywordServiceImpl;
import org.ysling.litemall.wx.model.search.body.SearchListBody;
import java.util.List;

/**
 * 关键字服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_keywords")
public class WxKeywordService extends KeywordServiceImpl {


    @Cacheable(sync = true)
    public LitemallKeyword queryDefault() {
        QueryWrapper<LitemallKeyword> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallKeyword.IS_DEFAULT , true);
        return getOne(wrapper , false);
    }


    @Cacheable(sync = true)
    public List<LitemallKeyword> queryHots() {
        QueryWrapper<LitemallKeyword> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallKeyword.IS_HOT , true);
        wrapper.orderByDesc(LitemallKeyword.WEIGHT);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallKeyword> queryByKeyword(SearchListBody body) {
        QueryWrapper<LitemallKeyword> wrapper = startPage(body);
        wrapper.like(LitemallKeyword.KEYWORD , body.getKeyword());
        wrapper.orderByDesc(LitemallKeyword.WEIGHT);
        return queryAll(wrapper);
    }

}
