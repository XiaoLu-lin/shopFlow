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
import org.ysling.litemall.admin.model.issue.body.IssueListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallIssue;
import org.ysling.litemall.db.service.impl.IssueServiceImpl;
import java.util.List;
import java.util.Objects;


/**
 * 常见问题服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_issue")
public class AdminIssueService extends IssueServiceImpl {


    public Object validate(LitemallIssue issue) {
        String question = issue.getQuestion();
        if (Objects.isNull(question)) {
            return ResponseUtil.badArgument();
        }
        String answer = issue.getAnswer();
        if (Objects.isNull(answer)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }
    
    @Cacheable(sync = true)
    public List<LitemallIssue> querySelective(IssueListBody body) {
        QueryWrapper<LitemallIssue> wrapper = startPage(body);
        if (StringUtils.hasText(body.getQuestion())) {
            wrapper.like(LitemallIssue.QUESTION , body.getQuestion());
        }
        return queryAll(wrapper);
    }


}
