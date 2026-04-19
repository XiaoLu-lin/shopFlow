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
import org.ysling.litemall.db.domain.LitemallAddress;
import org.ysling.litemall.db.service.impl.AddressServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收货地址服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_address")
public class WxAddressService extends AddressServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallAddress> queryByUid(String userId) {
        QueryWrapper<LitemallAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAddress.USER_ID , userId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public LitemallAddress query(String userId, String id) {
        QueryWrapper<LitemallAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAddress.USER_ID , userId);
        wrapper.eq(LitemallAddress.ID , id);
        return getOne(wrapper);
    }
    

    @Cacheable(sync = true)
    public void deleteByUser(String userId, String id) {
        QueryWrapper<LitemallAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAddress.USER_ID , userId);
        wrapper.eq(LitemallAddress.ID , id);
        remove(wrapper);
    }
    
    @Cacheable(sync = true)
    public LitemallAddress findDefault(String userId) {
        QueryWrapper<LitemallAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAddress.USER_ID , userId);
        wrapper.eq(LitemallAddress.IS_DEFAULT , true);
        return getOne(wrapper , false);
    }

    
    @CacheEvict(allEntries = true)
    public void resetDefault(String userId) {
        LitemallAddress address = new LitemallAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        QueryWrapper<LitemallAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallAddress.USER_ID , userId);
        update(address , wrapper);
    }


}
