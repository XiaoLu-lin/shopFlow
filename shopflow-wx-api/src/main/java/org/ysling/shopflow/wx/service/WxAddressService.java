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
import org.ysling.shopflow.db.domain.ShopflowAddress;
import org.ysling.shopflow.db.service.impl.AddressServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收货地址服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_address")
public class WxAddressService extends AddressServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowAddress> queryByUid(String userId) {
        QueryWrapper<ShopflowAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAddress.USER_ID , userId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public ShopflowAddress query(String userId, String id) {
        QueryWrapper<ShopflowAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAddress.USER_ID , userId);
        wrapper.eq(ShopflowAddress.ID , id);
        return getOne(wrapper);
    }
    

    @Cacheable(sync = true)
    public void deleteByUser(String userId, String id) {
        QueryWrapper<ShopflowAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAddress.USER_ID , userId);
        wrapper.eq(ShopflowAddress.ID , id);
        remove(wrapper);
    }
    
    @Cacheable(sync = true)
    public ShopflowAddress findDefault(String userId) {
        QueryWrapper<ShopflowAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAddress.USER_ID , userId);
        wrapper.eq(ShopflowAddress.IS_DEFAULT , true);
        return getOne(wrapper , false);
    }

    
    @CacheEvict(allEntries = true)
    public void resetDefault(String userId) {
        ShopflowAddress address = new ShopflowAddress();
        address.setIsDefault(false);
        address.setUpdateTime(LocalDateTime.now());
        QueryWrapper<ShopflowAddress> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowAddress.USER_ID , userId);
        update(address , wrapper);
    }


}
