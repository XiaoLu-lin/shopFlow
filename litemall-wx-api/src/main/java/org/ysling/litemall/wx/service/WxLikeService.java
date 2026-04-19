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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallLike;
import org.ysling.litemall.db.enums.LikeType;
import org.ysling.litemall.db.service.impl.LikeServiceImpl;

/**
 * 点赞服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_like")
public class WxLikeService extends LikeServiceImpl {

    
    @Cacheable(sync = true)
    public Integer count(Short type, String valueId) {
        QueryWrapper<LitemallLike> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallLike.VALUE_ID , valueId);
        wrapper.eq(LitemallLike.TYPE , type);
        wrapper.eq(LitemallLike.CANCEL , false);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public Boolean count(LikeType constant, String valueId , String userId) {
        if (userId == null) {
            return false;
        }
        QueryWrapper<LitemallLike> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallLike.TYPE , constant.getStatus());
        wrapper.eq(LitemallLike.VALUE_ID , valueId);
        wrapper.eq(LitemallLike.USER_ID , userId);
        wrapper.eq(LitemallLike.CANCEL , false);
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public LitemallLike query(Short type, String valueId , String userId) {
        QueryWrapper<LitemallLike> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallLike.TYPE , type);
        wrapper.eq(LitemallLike.VALUE_ID , valueId);
        wrapper.eq(LitemallLike.USER_ID , userId);
        return getOne(wrapper , false);
    }

}
