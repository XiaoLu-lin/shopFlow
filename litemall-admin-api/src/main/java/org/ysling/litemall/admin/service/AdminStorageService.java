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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.storage.body.StorageListBody;
import org.ysling.litemall.db.domain.LitemallStorage;
import org.ysling.litemall.db.service.impl.StorageServiceImpl;
import java.util.List;

/**
 * 对象存储服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_storage")
public class AdminStorageService extends StorageServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallStorage> querySelective(StorageListBody body) {
        QueryWrapper<LitemallStorage> wrapper = startPage(body);
        if (StringUtils.hasText(body.getKey())) {
            wrapper.eq(LitemallStorage.KEY , body.getKey());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallStorage.NAME , body.getName());
        }
        return queryAll(wrapper);
    }

    @CacheEvict(allEntries = true)
    public void deleteByKey(String key) {
        QueryWrapper<LitemallStorage> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallStorage.KEY , key);
        remove(wrapper);
    }

}
