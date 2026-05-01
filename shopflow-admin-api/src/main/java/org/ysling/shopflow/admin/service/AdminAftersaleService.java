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
import org.ysling.shopflow.admin.model.aftersale.body.AftersaleListBody;
import org.ysling.shopflow.db.domain.ShopflowAftersale;
import org.ysling.shopflow.db.service.impl.AftersaleServiceImpl;
import java.util.List;

/**
 * 售后服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_aftersale")
public class AdminAftersaleService extends AftersaleServiceImpl {


    @Cacheable(sync = true)
    public ShopflowAftersale findById(String userId, String id) {
        QueryWrapper<ShopflowAftersale> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAftersale.ID , id);
        wrapper.eq(ShopflowAftersale.USER_ID , userId);
        return getOne(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<ShopflowAftersale> querySelective(AftersaleListBody body) {
        QueryWrapper<ShopflowAftersale> wrapper = startPage(body);
        if (body.getStatus() != null) {
            wrapper.eq(ShopflowAftersale.STATUS , body.getStatus());
        }
        if (body.getOrderId() != null) {
            wrapper.eq(ShopflowAftersale.ORDER_ID , body.getOrderId());
        }
        if (StringUtils.hasText(body.getAftersaleSn())) {
            wrapper.eq(ShopflowAftersale.AFTERSALE_SN , body.getAftersaleSn());
        }
        return queryAll(wrapper);
    }

}
