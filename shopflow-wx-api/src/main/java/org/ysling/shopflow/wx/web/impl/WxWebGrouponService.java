package org.ysling.shopflow.wx.web.impl;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowGroupon;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.ysling.shopflow.wx.model.groupon.result.GrouponJoinResult;
import org.ysling.shopflow.wx.model.groupon.result.GrouponRuleResult;
import org.ysling.shopflow.wx.service.WxGoodsService;
import org.ysling.shopflow.wx.service.WxGrouponRulesService;
import org.ysling.shopflow.wx.service.WxGrouponService;
import java.util.ArrayList;
import java.util.List;


/**
 * 团购服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebGrouponService {

    @Autowired
    private WxGrouponRulesService rulesService;
    @Autowired
    private WxGrouponService grouponService;
    @Autowired
    private WxGoodsService goodsService;

    /**
     * 团购规则列表
     */
    public Object list(PageBody body) {
        List<ShopflowGrouponRules> grouponRulesList = rulesService.queryList(body);
        ArrayList<GrouponRuleResult> grouponList = new ArrayList<>();
        for (ShopflowGrouponRules rule : grouponRulesList) {
            String goodsId = rule.getGoodsId();
            ShopflowGoods goods = goodsService.findById(goodsId);
            if (goods == null || !GoodsStatus.getIsOnSale(goods)) {
                continue;
            }
            GrouponRuleResult result = new GrouponRuleResult();
            result.setId(goods.getId());
            result.setName(goods.getName());
            result.setBrief(goods.getBrief());
            result.setPicUrl(goods.getPicUrl());
            result.setCounterPrice(goods.getCounterPrice());
            result.setRetailPrice(goods.getRetailPrice());
            result.setGrouponPrice(goods.getRetailPrice().subtract(rule.getDiscount()));
            result.setGrouponDiscount(rule.getDiscount());
            result.setGrouponMember(rule.getDiscountMember());
            result.setExpireTime(rule.getExpireTime());
            grouponList.add(result);
        }
        return ResponseUtil.okList(grouponList, grouponRulesList);
    }

    /**
     * 参加团购
     * @param grouponId 团购活动ID
     * @return 操作结果
     */
    public Object join(String grouponId) {
        ShopflowGroupon groupon = grouponService.findById(grouponId);
        if (groupon == null) {
            return ResponseUtil.badArgumentValue();
        }

        ShopflowGrouponRules rules = rulesService.findById(groupon.getRulesId());
        if (rules == null) {
            return ResponseUtil.badArgumentValue();
        }

        ShopflowGoods goods = goodsService.findById(rules.getGoodsId());
        if (goods == null) {
            return ResponseUtil.badArgumentValue();
        }

        GrouponJoinResult result = new GrouponJoinResult();
        result.setGroupon(groupon);
        result.setGoodId(goods.getId());
        return ResponseUtil.ok(result);
    }

}
