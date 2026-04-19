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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallFootprint;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.service.impl.FootprintServiceImpl;
import java.util.List;

/**
 * 用户浏览足迹
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_footprint")
public class WxFootprintService extends FootprintServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallFootprint> queryByAddTime(String userId, PageBody body) {
        QueryWrapper<LitemallFootprint> wrapper = startPage(body);
        wrapper.eq(LitemallFootprint.USER_ID , userId);
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public LitemallFootprint findById(String userId, String id) {
        QueryWrapper<LitemallFootprint> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallFootprint.USER_ID , userId);
        wrapper.eq(LitemallFootprint.ID , id);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallFootprint findByGoodId(String userId, String goodId) {
        QueryWrapper<LitemallFootprint> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallFootprint.USER_ID , userId);
        wrapper.eq(LitemallFootprint.GOODS_ID , goodId);
        return getOne(wrapper , false);
    }


    @CacheEvict(allEntries = true)
    public void createFootprint(String userId, LitemallGoods goods) {
        if (userId == null) {
            return;
        }
        LitemallFootprint footprint = findByGoodId(userId, goods.getId());
        if (footprint != null){
            footprint.setName(goods.getName());
            footprint.setBrief(goods.getBrief());
            footprint.setPicUrl(goods.getPicUrl());
            footprint.setRetailPrice(goods.getRetailPrice());
            updateVersionSelective(footprint);
        }else {
            footprint = new LitemallFootprint();
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
