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
import org.ysling.litemall.db.domain.LitemallCollect;
import org.ysling.litemall.db.enums.CollectType;
import org.ysling.litemall.db.service.impl.CollectServiceImpl;
import org.ysling.litemall.wx.model.collect.body.CollectListBody;
import java.util.List;


/**
 * 收藏服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_collect")
public class WxCollectService extends CollectServiceImpl {

    
    @Cacheable(sync = true)
    public Boolean count(String userId, CollectType type, String goodsId) {
        if (userId == null) {
            return false;
        }
        QueryWrapper<LitemallCollect> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCollect.USER_ID , userId);
        wrapper.eq(LitemallCollect.TYPE , type.getStatus());
        wrapper.eq(LitemallCollect.VALUE_ID , goodsId);
        wrapper.eq(LitemallCollect.CANCEL , false);
        return exists(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallCollect> queryByType(String userId, CollectListBody body) {
        QueryWrapper<LitemallCollect> wrapper = startPage(body);
        wrapper.eq(LitemallCollect.USER_ID , userId);
        wrapper.eq(LitemallCollect.CANCEL , false);
        if (body.getType() != null) {
            wrapper.eq(LitemallCollect.TYPE , body.getType());
        }
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public LitemallCollect queryByTypeAndValue(String userId, Byte type, String valueId) {
        QueryWrapper<LitemallCollect> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCollect.USER_ID , userId);
        wrapper.eq(LitemallCollect.TYPE , type);
        wrapper.eq(LitemallCollect.VALUE_ID , valueId);
        return getOne(wrapper);
    }

}
