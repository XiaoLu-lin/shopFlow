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
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.service.impl.AftersaleServiceImpl;
import org.ysling.litemall.wx.model.aftersale.body.AftersaleListBody;
import java.util.List;

/**
 * 售后服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_aftersale")
public class WxAftersaleService extends AftersaleServiceImpl {

    
    @Cacheable(sync = true)
    public LitemallAftersale findById(String userId, String id) {
        QueryWrapper<LitemallAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAftersale.ID , id);
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        return getOne(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<LitemallAftersale> querySelective(String userId, AftersaleListBody body) {
        QueryWrapper<LitemallAftersale> wrapper = startPage(body);
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        if (body.getStatus() != null) {
            wrapper.eq(LitemallAftersale.STATUS , body.getStatus());
        }
        return queryAll(wrapper);
    }
    
    @Cacheable(sync = true)
    public Boolean countByAftersaleSn(String userId, String aftersaleSn) {
        QueryWrapper<LitemallAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        wrapper.eq(LitemallAftersale.AFTERSALE_SN , aftersaleSn);
        return exists(wrapper);
    }
    
    @CacheEvict(allEntries = true)
    public void deleteByOrderId(String userId, String orderId) {
        QueryWrapper<LitemallAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        wrapper.eq(LitemallAftersale.ORDER_ID , orderId);
        remove(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallAftersale findByOrderId(String userId, String orderId) {
        QueryWrapper<LitemallAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        wrapper.eq(LitemallAftersale.ORDER_ID , orderId);
        return getOne(wrapper);
    }

}
