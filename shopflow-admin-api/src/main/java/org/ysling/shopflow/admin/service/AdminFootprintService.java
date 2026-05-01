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
import org.ysling.shopflow.admin.model.footprint.body.FootprintListBody;
import org.ysling.shopflow.db.domain.ShopflowFootprint;
import org.ysling.shopflow.db.service.impl.FootprintServiceImpl;
import java.util.List;

/**
 * 用户浏览足迹服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_footprint")
public class AdminFootprintService extends FootprintServiceImpl {



    @Cacheable(sync = true)
    public List<ShopflowFootprint> querySelective(FootprintListBody body) {
        QueryWrapper<ShopflowFootprint> wrapper = startPage(body);
        if (StringUtils.hasText(body.getUserId())) {
            wrapper.eq(ShopflowFootprint.USER_ID , body.getUserId());
        }
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(ShopflowFootprint.GOODS_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }


}
