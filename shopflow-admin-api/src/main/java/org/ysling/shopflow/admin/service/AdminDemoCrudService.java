package org.ysling.shopflow.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.democrud.body.DemoCrudListBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowIssue;
import org.ysling.shopflow.db.service.impl.IssueServiceImpl;

import java.util.List;
import java.util.Objects;

/**
 * CRUD 练习服务（基于 shopflow_issue 表）
 */
@Service
@CacheConfig(cacheNames = "shopflow_demo_crud")
public class AdminDemoCrudService extends IssueServiceImpl {

    public Object validate(ShopflowIssue issue) {
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
    public List<ShopflowIssue> querySelective(DemoCrudListBody body) {
        QueryWrapper<ShopflowIssue> wrapper = startPage(body);
        if (StringUtils.hasText(body.getQuestion())) {
            wrapper.like(ShopflowIssue.QUESTION, body.getQuestion());
        }
        return queryAll(wrapper);
    }
}
