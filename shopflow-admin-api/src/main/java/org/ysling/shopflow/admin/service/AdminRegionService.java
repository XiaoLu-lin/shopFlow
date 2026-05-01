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
import org.ysling.shopflow.db.domain.ShopflowRegion;
import org.ysling.shopflow.db.service.impl.RegionServiceImpl;
import java.util.List;

/**
 * 行政区域服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_region")
public class AdminRegionService extends RegionServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowRegion> getAll(){
        QueryWrapper<ShopflowRegion> wrapper = new QueryWrapper<>();
        wrapper.ne(ShopflowRegion.TYPE , 4);
        return queryAll(wrapper);
    }
    
    @Cacheable(sync = true)
    public List<ShopflowRegion> queryByPid(String parentId) {
        QueryWrapper<ShopflowRegion> wrapper = new QueryWrapper<>();
        wrapper.ne(ShopflowRegion.PID , parentId);
        return queryAll(wrapper);
    }

}
