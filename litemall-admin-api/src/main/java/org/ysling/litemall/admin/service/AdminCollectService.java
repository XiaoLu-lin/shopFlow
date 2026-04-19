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
import org.ysling.litemall.admin.model.collect.body.CollectListBody;
import org.ysling.litemall.db.domain.LitemallCollect;
import org.ysling.litemall.db.service.impl.CollectServiceImpl;
import java.util.List;

/**
 * 用户收藏服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_collect")
public class AdminCollectService extends CollectServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallCollect> querySelective(CollectListBody body) {
        QueryWrapper<LitemallCollect> wrapper = startPage(body);
        wrapper.eq(LitemallCollect.CANCEL , false);
        if (StringUtils.hasText(body.getUserId())) {
            wrapper.eq(LitemallCollect.USER_ID , body.getUserId());
        }
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(LitemallCollect.VALUE_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }


}
