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
import org.ysling.litemall.admin.model.comment.body.CommentListBody;
import org.ysling.litemall.db.domain.LitemallGoodsComment;
import org.ysling.litemall.db.service.impl.GoodsCommentServiceImpl;
import java.util.List;

/**
 * 商品评论服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods_comment")
public class AdminGoodsCommentService extends GoodsCommentServiceImpl {


    
    @Cacheable(sync = true)
    public List<LitemallGoodsComment> querySelective(CommentListBody body) {
        QueryWrapper<LitemallGoodsComment> wrapper = startPage(body);
        if (StringUtils.hasText(body.getUserId())) {
            wrapper.eq(LitemallGoodsComment.USER_ID , body.getUserId());
        }
        if (StringUtils.hasText(body.getGoodsId())) {
            wrapper.eq(LitemallGoodsComment.GOODS_ID , body.getGoodsId());
        }
        return queryAll(wrapper);
    }


}
