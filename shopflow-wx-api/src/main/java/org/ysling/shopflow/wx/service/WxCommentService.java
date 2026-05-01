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
import org.ysling.shopflow.db.domain.ShopflowComment;
import org.ysling.shopflow.db.service.impl.CommentServiceImpl;
import org.ysling.shopflow.wx.model.comment.body.CommentListBody;
import org.ysling.shopflow.wx.model.comment.body.CommentReplyListBody;
import java.util.List;

/**
 * 评论服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_comment")
public class WxCommentService extends CommentServiceImpl {


    @Cacheable(sync = true)
    public Integer count(Short type, String valueId) {
        QueryWrapper<ShopflowComment> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowComment.TYPE , type);
        wrapper.eq(ShopflowComment.VALUE_ID , valueId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public Integer replyCount(String commentId) {
        QueryWrapper<ShopflowComment> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowComment.REPLY_ID , commentId);
        return Math.toIntExact(count(wrapper));
    }

    
    @Cacheable(sync = true)
    public List<ShopflowComment> queryReplySelective(CommentReplyListBody body) {
        QueryWrapper<ShopflowComment> wrapper = startPage(body);
        wrapper.eq(ShopflowComment.REPLY_ID , body.getCommentId());
        wrapper.orderByDesc(ShopflowComment.LIKE_COUNT);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowComment> querySelective(CommentListBody body) {
        QueryWrapper<ShopflowComment> wrapper = startPage(body);
        wrapper.eq(ShopflowComment.TYPE , body.getCommentType());
        wrapper.eq(ShopflowComment.VALUE_ID , body.getValueId());
        wrapper.isNull(ShopflowComment.REPLY_ID);
        return queryAll(wrapper);
    }


}
