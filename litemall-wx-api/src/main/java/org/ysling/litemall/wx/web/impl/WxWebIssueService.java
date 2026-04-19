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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.wx.model.issue.body.IssueListBody;
import org.ysling.litemall.wx.service.WxIssueService;

/**
 * 常见问题服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebIssueService {

    @Autowired
    private WxIssueService issueService;

    /**
     * 帮助中心
     */
    public Object list(IssueListBody body) {
        return ResponseUtil.okList(issueService.querySelective(body));
    }

}
