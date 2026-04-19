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
import org.ysling.litemall.admin.model.aftersale.body.AftersaleListBody;
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.service.impl.AftersaleServiceImpl;
import java.util.List;

/**
 * 售后服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_aftersale")
public class AdminAftersaleService extends AftersaleServiceImpl {


    @Cacheable(sync = true)
    public LitemallAftersale findById(String userId, String id) {
        QueryWrapper<LitemallAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAftersale.ID , id);
        wrapper.eq(LitemallAftersale.USER_ID , userId);
        return getOne(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<LitemallAftersale> querySelective(AftersaleListBody body) {
        QueryWrapper<LitemallAftersale> wrapper = startPage(body);
        if (body.getStatus() != null) {
            wrapper.eq(LitemallAftersale.STATUS , body.getStatus());
        }
        if (body.getOrderId() != null) {
            wrapper.eq(LitemallAftersale.ORDER_ID , body.getOrderId());
        }
        if (StringUtils.hasText(body.getAftersaleSn())) {
            wrapper.eq(LitemallAftersale.AFTERSALE_SN , body.getAftersaleSn());
        }
        return queryAll(wrapper);
    }

}
