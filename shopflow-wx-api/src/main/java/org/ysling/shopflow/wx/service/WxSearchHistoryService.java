package org.ysling.shopflow.wx.service;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowSearchHistory;
import org.ysling.shopflow.db.service.impl.SearchHistoryServiceImpl;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索历史记录
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_search_history")
public class WxSearchHistoryService extends SearchHistoryServiceImpl {

    
    @Cacheable(sync = true)
    public ShopflowSearchHistory findByKeyword(String userId ,String keyword) {
        QueryWrapper<ShopflowSearchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowSearchHistory.USER_ID , userId);
        wrapper.eq(ShopflowSearchHistory.KEYWORD , keyword);
        return getOne(wrapper , false);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowSearchHistory> queryByUid(String userId) {
        if (userId == null){
            return new ArrayList<>();
        }
        QueryWrapper<ShopflowSearchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowSearchHistory.USER_ID , userId);
        return queryAll(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void deleteByUid(String userId) {
        QueryWrapper<ShopflowSearchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowSearchHistory.USER_ID , userId);
        remove(wrapper);
    }

}
