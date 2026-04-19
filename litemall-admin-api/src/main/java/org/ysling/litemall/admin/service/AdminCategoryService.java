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
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallCategory;
import org.ysling.litemall.db.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 分类服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_category")
public class AdminCategoryService extends CategoryServiceImpl {


    public Object validate(LitemallCategory category) {
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
    public List<LitemallCategory> queryL1() {
        QueryWrapper<LitemallCategory> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCategory.LEVEL , "L1");
        wrapper.orderByDesc(LitemallCategory.WEIGHT);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallCategory> queryByPid(String pid) {
        QueryWrapper<LitemallCategory> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCategory.PID , pid);
        wrapper.orderByDesc(LitemallCategory.WEIGHT);
        return queryAll(wrapper);
    }

}
