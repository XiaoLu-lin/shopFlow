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
import org.ysling.shopflow.db.domain.ShopflowCollect;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowTopic;
import org.ysling.shopflow.db.enums.CollectType;
import org.ysling.shopflow.wx.model.collect.body.CollectListBody;
import org.ysling.shopflow.wx.model.collect.body.CollectUpdateBody;
import org.ysling.shopflow.wx.service.WxCollectService;
import org.ysling.shopflow.wx.service.WxGoodsService;
import org.ysling.shopflow.wx.service.WxTopicService;
import java.util.Objects;

/**
 * 用户收藏服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebCollectService {

    @Autowired
    private WxCollectService collectService;
    @Autowired
    private WxGoodsService goodsService;
    @Autowired
    private WxTopicService topicService;

    /**
     * 用户收藏列表
     */
    public Object list(String userId, CollectListBody body) {
        return ResponseUtil.okList(collectService.queryByType(userId, body));
    }

    /**
     * 用户收藏添加或删除
     * <p>
     * 如果商品没有收藏，则添加收藏；如果商品已经收藏，则删除收藏状态。
     *
     * @param userId 用户ID
     * @param body   请求内容，{ type: xxx, valueId: xxx }
     * @return 操作结果 update
     */
    public Object update(String userId, CollectUpdateBody body) {
        ShopflowCollect collect = collectService.queryByTypeAndValue(userId, body.getType(), body.getValueId());
        if (collect != null) {
            collect.setCancel(!collect.getCancel());
            collectService.updateSelective(collect);
        } else {
            collect = new ShopflowCollect();
            if (Objects.equals(body.getType() , CollectType.TYPE_GOODS.getStatus())){
                //商品收藏
                ShopflowGoods goods = goodsService.findById(body.getValueId());
                if (goods == null) {
                    return ResponseUtil.badArgument();
                }
                collect.setPicUrl(goods.getPicUrl());
                collect.setName(goods.getName());
                collect.setBrief(goods.getBrief());
                collect.setPrice(goods.getRetailPrice());
            } else if (Objects.equals(body.getType() , CollectType.TYPE_TOPIC.getStatus())){
                //专题收藏
                ShopflowTopic topic = topicService.findById(body.getValueId());
                if (topic == null) {
                    return ResponseUtil.badArgument();
                }
                collect.setPicUrl(topic.getPicUrl());
                collect.setName(topic.getTitle());
                collect.setBrief(topic.getSubtitle());
                collect.setPrice(topic.getPrice());
            } else {
                return ResponseUtil.badArgument();
            }
            collect.setUserId(userId);
            collect.setValueId(body.getValueId());
            collect.setType(body.getType());
            collect.setCancel(false);
            collectService.add(collect);
        }
        return ResponseUtil.ok();
    }


}