package org.ysling.litemall.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.democrud.body.DemoCrudListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallIssue;
import org.ysling.litemall.db.service.impl.IssueServiceImpl;

import java.util.List;
import java.util.Objects;

/**
 * CRUD 练习服务（基于 litemall_issue 表）
 */
@Service
@CacheConfig(cacheNames = "litemall_demo_crud")
public class AdminDemoCrudService extends IssueServiceImpl {

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
    public List<LitemallIssue> querySelective(DemoCrudListBody body) {
        QueryWrapper<LitemallIssue> wrapper = startPage(body);
        if (StringUtils.hasText(body.getQuestion())) {
            wrapper.like(LitemallIssue.QUESTION, body.getQuestion());
        }
        return queryAll(wrapper);
    }
}
