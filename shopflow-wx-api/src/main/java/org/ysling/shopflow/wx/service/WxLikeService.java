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
import org.ysling.shopflow.db.domain.ShopflowLike;
import org.ysling.shopflow.db.enums.LikeType;
import org.ysling.shopflow.db.service.impl.LikeServiceImpl;

/**
 * 点赞服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_like")
public class WxLikeService extends LikeServiceImpl {

    
    @Cacheable(sync = true)
    public Integer count(Short type, String valueId) {
        QueryWrapper<ShopflowLike> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowLike.VALUE_ID , valueId);
        wrapper.eq(ShopflowLike.TYPE , type);
        wrapper.eq(ShopflowLike.CANCEL , false);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public Boolean count(LikeType constant, String valueId , String userId) {
        if (userId == null) {
            return false;
        }
        QueryWrapper<ShopflowLike> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowLike.TYPE , constant.getStatus());
        wrapper.eq(ShopflowLike.VALUE_ID , valueId);
        wrapper.eq(ShopflowLike.USER_ID , userId);
        wrapper.eq(ShopflowLike.CANCEL , false);
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public ShopflowLike query(Short type, String valueId , String userId) {
        QueryWrapper<ShopflowLike> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowLike.TYPE , type);
        wrapper.eq(ShopflowLike.VALUE_ID , valueId);
        wrapper.eq(ShopflowLike.USER_ID , userId);
        return getOne(wrapper , false);
    }

}
