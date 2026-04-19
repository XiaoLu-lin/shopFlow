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
import org.ysling.litemall.db.domain.LitemallGrouponRules;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.GrouponRuleStatus;
import org.ysling.litemall.db.service.impl.GrouponRulesServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团购规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_groupon_rules")
public class WxGrouponRulesService extends GrouponRulesServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallGrouponRules> queryOnByGoodsId(String goodsId) {
        QueryWrapper<LitemallGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGrouponRules.GOODS_ID , goodsId);
        wrapper.eq(LitemallGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<LitemallGrouponRules> queryByGoodsId(String goodsId) {
        QueryWrapper<LitemallGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGrouponRules.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallGrouponRules> queryByGroupon(Integer limit) {
        QueryWrapper<LitemallGrouponRules> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<LitemallGrouponRules> queryList(PageBody body) {
        QueryWrapper<LitemallGrouponRules> wrapper = startPage(body);
        wrapper.eq(LitemallGrouponRules.STATUS, GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public boolean isExpired(LitemallGrouponRules rules) {
        return (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()));
    }

}