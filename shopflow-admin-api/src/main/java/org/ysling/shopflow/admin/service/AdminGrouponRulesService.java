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
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.groupon.body.GrouponRuleListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.service.impl.GrouponRulesServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团购规则服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_groupon_rules")
public class AdminGrouponRulesService extends GrouponRulesServiceImpl {


    public Object validate(ShopflowGrouponRules grouponRules) {
        String goodsId = grouponRules.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.badArgument();
        }
        BigDecimal discount = grouponRules.getDiscount();
        if (discount == null) {
            return ResponseUtil.badArgument();
        }
        Integer discountMember = grouponRules.getDiscountMember();
        if (discountMember == null) {
            return ResponseUtil.badArgument();
        }
        LocalDateTime expireTime = grouponRules.getExpireTime();
        if (expireTime == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @Cacheable(sync = true)
    public ShopflowGrouponRules findByGid(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public Integer countByGoodsId(String goodsId){
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        return Math.toIntExact(count(wrapper));
    }

    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> queryByGoodsId(String goodsId) {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<ShopflowGrouponRules> querySelective(GrouponRuleListBody body) {
        QueryWrapper<ShopflowGrouponRules> wrapper = startPage(body);
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(ShopflowGrouponRules.GOODS_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }


}