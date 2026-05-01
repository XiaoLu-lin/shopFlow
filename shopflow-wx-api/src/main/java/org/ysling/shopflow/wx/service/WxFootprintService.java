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
import org.ysling.shopflow.db.domain.ShopflowFootprint;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.service.impl.FootprintServiceImpl;
import java.util.List;

/**
 * 用户浏览足迹
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_footprint")
public class WxFootprintService extends FootprintServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowFootprint> queryByAddTime(String userId, PageBody body) {
        QueryWrapper<ShopflowFootprint> wrapper = startPage(body);
        wrapper.eq(ShopflowFootprint.USER_ID , userId);
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public ShopflowFootprint findById(String userId, String id) {
        QueryWrapper<ShopflowFootprint> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowFootprint.USER_ID , userId);
        wrapper.eq(ShopflowFootprint.ID , id);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public ShopflowFootprint findByGoodId(String userId, String goodId) {
        QueryWrapper<ShopflowFootprint> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowFootprint.USER_ID , userId);
        wrapper.eq(ShopflowFootprint.GOODS_ID , goodId);
        return getOne(wrapper , false);
    }


    @CacheEvict(allEntries = true)
    public void createFootprint(String userId, ShopflowGoods goods) {
        if (userId == null) {
            return;
        }
        ShopflowFootprint footprint = findByGoodId(userId, goods.getId());
        if (footprint != null){
            footprint.setName(goods.getName());
            footprint.setBrief(goods.getBrief());
            footprint.setPicUrl(goods.getPicUrl());
            footprint.setRetailPrice(goods.getRetailPrice());
            updateVersionSelective(footprint);
        }else {
            footprint = new ShopflowFootprint();
            footprint.setUserId(userId);
            footprint.setGoodsId(goods.getId());
            footprint.setName(goods.getName());
            footprint.setBrief(goods.getBrief());
            footprint.setPicUrl(goods.getPicUrl());
            footprint.setRetailPrice(goods.getRetailPrice());
            add(footprint);
        }
    }

}
