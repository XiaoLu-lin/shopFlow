package org.ysling.litemall.wx.web.impl;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.domain.LitemallTopic;
import org.ysling.litemall.db.enums.CollectType;
import org.ysling.litemall.db.enums.LikeType;
import org.ysling.litemall.wx.annotation.LoginUser;
import org.ysling.litemall.wx.model.topic.body.TopicListBody;
import org.ysling.litemall.wx.model.topic.result.TopicDetailResult;
import org.ysling.litemall.wx.service.WxCollectService;
import org.ysling.litemall.wx.service.WxGoodsService;
import org.ysling.litemall.wx.service.WxLikeService;
import org.ysling.litemall.wx.service.WxTopicService;
import org.ysling.litemall.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;

/**
 * 专题服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebTopicService {

    @Autowired
    private WxTopicService topicService;
    @Autowired
    private WxGoodsService goodsService;
	@Autowired
	private WxCollectService collectService;
    @Autowired
    private WxLikeService likeService;

    /**
     * 专题列表
     */
    public Object list(TopicListBody body) {
        return ResponseUtil.okList(topicService.queryList(body));
    }

    /**
     * 专题详情
     * @param id 专题ID
     * @return 专题详情
     */
    public Object detail(@LoginUser String userId, @JsonBody String id) {
        LitemallTopic topic = topicService.findById(id);
        List<LitemallGoods> goods = new ArrayList<>();
        for (String i : topic.getGoodsIds()) {
            LitemallGoods good = goodsService.findByIdVO(i);
            if (null != good) {
                goods.add(good);
            }
        }
        TopicDetailResult result = new TopicDetailResult();
        result.setTopic(topic);
        result.setGoods(goods);
        result.setTopicLike(likeService.count(LikeType.TYPE_TOPIC, topic.getId(), userId));
        result.setUserHasCollect(collectService.count(userId, CollectType.TYPE_TOPIC, id));
        return ResponseUtil.ok(result);
    }

    /**
     * 相关专题
     * @param id 专题ID
     * @return 相关专题
     */
    public Object related(@JsonBody String id) {
        return ResponseUtil.okList(topicService.queryRelatedList(id, 4));
    }

}