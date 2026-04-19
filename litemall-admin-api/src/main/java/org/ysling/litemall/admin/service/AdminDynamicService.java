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
import org.ysling.litemall.admin.model.dynamic.body.DynamicListBody;
import org.ysling.litemall.db.domain.LitemallDynamic;
import org.ysling.litemall.db.service.impl.DynamicServiceImpl;
import java.util.List;

/**
 * 动态服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_dynamic")
public class AdminDynamicService extends DynamicServiceImpl {

    
    @Cacheable(sync = true)
    public List<LitemallDynamic> querySelective(DynamicListBody body) {
        QueryWrapper<LitemallDynamic> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(LitemallDynamic.USER_ID , body.getUserId());
        }
        if (StringUtils.hasText(body.getContent())){
            wrapper.like(LitemallDynamic.CONTENT , body.getContent());
        }
        return queryAll(wrapper);
    }


}
