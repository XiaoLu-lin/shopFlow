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
import org.ysling.litemall.admin.model.system.body.SystemListBody;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallSystem;
import org.ysling.litemall.db.service.impl.SystemServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 系统配置
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_system")
public class AdminSystemService extends SystemServiceImpl {


    /**
     * 参数校验
     */
    public Object validate(LitemallSystem system) {
        if (!StringUtils.hasText(system.getName())) {
            return ResponseUtil.fail("配置名称不能为空");
        }
        if (!StringUtils.hasText(system.getValue())) {
            return ResponseUtil.fail("配置值不能为空");
        }
        if (!StringUtils.hasText(system.getDepict())) {
            return ResponseUtil.fail("配置描述不能为空");
        }
        LitemallSystem name = findByName(system.getName());
        if (name != null && !Objects.equals(name.getId(), system.getId())){
            return ResponseUtil.fail("配置名称不能重复");
        }
        system.setValue(SystemUtils.checkValue(system.getName(), system.getValue()).toString());
        return null;
    }

    @Cacheable(sync = true)
    public LitemallSystem findByName(String name) {
        QueryWrapper<LitemallSystem> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallSystem.NAME, name);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public List<LitemallSystem> querySelective(SystemListBody body) {
        QueryWrapper<LitemallSystem> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(body.getName())){
            wrapper.likeRight(LitemallSystem.NAME, body.getName());
        }
        return queryAll(wrapper);
    }
    

}
