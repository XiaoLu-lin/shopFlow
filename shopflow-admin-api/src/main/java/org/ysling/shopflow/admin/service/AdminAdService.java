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
import org.ysling.shopflow.admin.model.ad.body.AdListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowAd;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.service.impl.AdServiceImpl;
import java.util.List;
import java.util.Objects;

/**
 * 广告服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_ad")
public class AdminAdService extends AdServiceImpl {


    public Object validate(ShopflowAd ad) {
        String name = ad.getName();
        if (Objects.isNull(name)) {
            return ResponseUtil.badArgument();
        }
        String content = ad.getContent();
        if (Objects.isNull(content)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
    

    @Cacheable(sync = true)
    public List<ShopflowAd> querySelective(AdListBody body) {
        QueryWrapper<ShopflowAd> wrapper = startPage(body);
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(ShopflowAd.NAME , body.getName());
        }
        if (StringUtils.hasText(body.getContent())) {
            wrapper.like(ShopflowAd.CONTENT , body.getContent());
        }
        wrapper.orderByDesc(ShopflowAd.WEIGHT);
        return list(wrapper);
    }


}
