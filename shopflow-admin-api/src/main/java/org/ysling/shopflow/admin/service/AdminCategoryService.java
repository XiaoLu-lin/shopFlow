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
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowCategory;
import org.ysling.shopflow.db.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 分类服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_category")
public class AdminCategoryService extends CategoryServiceImpl {


    public Object validate(ShopflowCategory category) {
        String name = category.getName();
        if (Objects.isNull(name)) {
            return ResponseUtil.badArgument();
        }
        String level = category.getLevel();
        if (Objects.isNull(level)) {
            return ResponseUtil.badArgument();
        }
        if (!"L1".equals(level) && !"L2".equals(level)) {
            return ResponseUtil.badArgumentValue();
        }
        String pid = category.getPid();
        if ("L2".equals(level) && (pid == null)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @Cacheable(sync = true)
    public List<ShopflowCategory> queryL1() {
        QueryWrapper<ShopflowCategory> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCategory.LEVEL , "L1");
        wrapper.orderByDesc(ShopflowCategory.WEIGHT);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowCategory> queryByPid(String pid) {
        QueryWrapper<ShopflowCategory> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCategory.PID , pid);
        wrapper.orderByDesc(ShopflowCategory.WEIGHT);
        return queryAll(wrapper);
    }

}
