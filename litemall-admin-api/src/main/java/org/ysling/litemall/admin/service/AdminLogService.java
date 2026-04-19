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
import org.ysling.litemall.admin.model.log.body.LogListBody;
import org.ysling.litemall.db.domain.LitemallLog;
import org.ysling.litemall.db.service.impl.LogServiceImpl;
import java.util.List;

/**
 * 日志服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_log")
public class AdminLogService extends LogServiceImpl {

    
    @Cacheable(sync = true)
    public List<LitemallLog> querySelective(LogListBody body) {
        QueryWrapper<LitemallLog> wrapper = startPage(body);
        if (body.getType() != null) {
            wrapper.eq(LitemallLog.TYPE , body.getType());
        }
        if (body.getStatus() != null) {
            wrapper.eq(LitemallLog.STATUS , body.getStatus());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallLog.ADMIN , body.getName());
        }
        return queryAll(wrapper);
    }


}
