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
import org.ysling.litemall.admin.model.groupon.body.GrouponListBody;
import org.ysling.litemall.db.domain.LitemallGroupon;
import org.ysling.litemall.db.service.impl.GrouponServiceImpl;
import java.util.List;

/**
 * 团购活动服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_groupon")
public class AdminGrouponService extends GrouponServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallGroupon> querySelective(GrouponListBody body) {
        QueryWrapper<LitemallGroupon> wrapper = startPage(body);
        if (body.getUserId() != null) {
            wrapper.eq(LitemallGroupon.USER_ID , body.getUserId());
        }
        if(body.getRuleId() != null){
            wrapper.eq(LitemallGroupon.RULES_ID , body.getRuleId());
        }
        if (body.getStatus() != null) {
            wrapper.eq(LitemallGroupon.STATUS , body.getStatus());
        }
        if(body.getGrouponId() != null){
            wrapper.eq(LitemallGroupon.GROUPON_ID , body.getGrouponId());
        }
        return queryAll(wrapper);
    }


}
