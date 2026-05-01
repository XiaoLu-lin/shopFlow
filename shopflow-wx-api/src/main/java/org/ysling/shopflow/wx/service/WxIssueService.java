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
import org.springframework.util.StringUtils;
import org.ysling.shopflow.db.domain.ShopflowIssue;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.service.impl.IssueServiceImpl;
import org.ysling.shopflow.wx.model.issue.body.IssueListBody;
import java.util.List;


/**
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_issue")
public class WxIssueService extends IssueServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowIssue> getGoodsIssue() {
        return queryAll(startPage(new PageBody(4)));
    }
    
    @Cacheable(sync = true)
    public List<ShopflowIssue> querySelective(IssueListBody body) {
        QueryWrapper<ShopflowIssue> wrapper = startPage(body);
        if (StringUtils.hasText(body.getQuestion())) {
            wrapper.like(ShopflowIssue.QUESTION , body.getQuestion());
        }
        return queryAll(wrapper);
    }


}
