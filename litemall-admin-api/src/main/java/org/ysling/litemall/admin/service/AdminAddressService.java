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
import org.ysling.litemall.admin.model.address.body.AddressListBody;
import org.ysling.litemall.db.domain.LitemallAddress;
import org.ysling.litemall.db.service.impl.AddressServiceImpl;
import java.util.List;

/**
 * 用户收货地址
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_address")
public class AdminAddressService extends AddressServiceImpl {

    
    @Cacheable(sync = true)
    public List<LitemallAddress> querySelective(AddressListBody body) {
        QueryWrapper<LitemallAddress> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(LitemallAddress.USER_ID , body.getUserId());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallAddress.NAME , body.getName());
        }
        if (StringUtils.hasText(body.getMobile())) {
            wrapper.like(LitemallAddress.MOBILE , body.getMobile());
        }
        return queryAll(wrapper);
    }

}
