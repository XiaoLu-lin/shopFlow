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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallGoodsComment;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.entity.UserInfo;
import org.ysling.litemall.db.service.impl.GoodsCommentServiceImpl;
import org.ysling.litemall.wx.model.goods.body.GoodsCommentListBody;
import org.ysling.litemall.wx.model.goods.result.GoodsCommentInfo;
import org.ysling.litemall.wx.model.goods.result.GoodsCommentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品评论服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods_comment")
public class WxGoodsCommentService extends GoodsCommentServiceImpl {


    @Autowired
    private WxUserService userService;

    /**
     * 获取评论信息
     * @param goodsId 商品id
     * @param limit  条数
     */
    public GoodsCommentResult getComments(String goodsId , int limit){
        GoodsCommentResult result = new GoodsCommentResult();
        result.setCount(count(goodsId, false));
        result.setData(commentInfoList(queryCommentList(goodsId, limit)));
        return result;
    }

    /**
     * 获取评论列表
     * @param comments 评论列表
     */
    public List<GoodsCommentInfo> commentInfoList(List<LitemallGoodsComment> comments){
        List<GoodsCommentInfo> commentInfoList = new ArrayList<>(comments.size());
        for (LitemallGoodsComment comment : comments) {
            GoodsCommentInfo commentInfo = new GoodsCommentInfo();
            commentInfo.setGoodsId(comment.getGoodsId());
            commentInfo.setAddTime(comment.getAddTime());
            commentInfo.setContent(comment.getContent());
            commentInfo.setAdminContent(comment.getAdminContent());
            UserInfo user = userService.findUserVoById(comment.getUserId());
            commentInfo.setNickName(user.getNickName());
            commentInfo.setAvatarUrl(user.getAvatarUrl());
            commentInfo.setPicUrls(comment.getPicUrls());
            commentInfoList.add(commentInfo);
        }
        return commentInfoList;
    }

    
    @Cacheable(sync = true)
    public List<LitemallGoodsComment> querySelective(GoodsCommentListBody body) {
        QueryWrapper<LitemallGoodsComment> wrapper = startPage(body);
        wrapper.eq(LitemallGoodsComment.GOODS_ID , body.getGoodsId());
        if (body.getHasPicture() != null && body.getHasPicture()) {
            wrapper.eq(LitemallGoodsComment.HAS_PICTURE , true);
        }
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public List<LitemallGoodsComment> queryCommentList(String goodsId, Integer limit) {
        QueryWrapper<LitemallGoodsComment> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoodsComment.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public Integer count(String goodsId, Boolean hasPicture) {
        QueryWrapper<LitemallGoodsComment> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsComment.GOODS_ID , goodsId);
        if (hasPicture) {
            wrapper.eq(LitemallGoodsComment.HAS_PICTURE , true);
        }
        return Math.toIntExact(count(wrapper));
    }

}
