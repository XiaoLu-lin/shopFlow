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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.enums.GrouponRuleStatus;
import org.ysling.shopflow.db.service.impl.GrouponRulesServiceImpl;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团购规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_groupon_rules")
public class WxGrouponRulesService extends GrouponRulesServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> queryOnByGoodsId(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        wrapper.eq(ShopflowGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> queryByGoodsId(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> queryByGroupon(Integer limit) {
        QueryWrapper<ShopflowGrouponRules> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> queryList(PageBody body) {
        QueryWrapper<ShopflowGrouponRules> wrapper = startPage(body);
        wrapper.eq(ShopflowGrouponRules.STATUS, GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public boolean isExpired(ShopflowGrouponRules rules) {
        return (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()));
    }

}