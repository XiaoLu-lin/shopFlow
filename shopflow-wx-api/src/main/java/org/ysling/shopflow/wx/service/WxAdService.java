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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowAd;
import org.ysling.shopflow.db.service.impl.AdServiceImpl;
import java.util.List;

/**
 * 广告服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_ad")
public class WxAdService extends AdServiceImpl {

    
    @Cacheable(sync = true)
    public List<ShopflowAd> queryIndex() {
        QueryWrapper<ShopflowAd> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAd.POSITION , 1);
        wrapper.eq(ShopflowAd.ENABLED , true);
        wrapper.orderByDesc(ShopflowAd.WEIGHT);
        return queryAll(wrapper);
    }

}
