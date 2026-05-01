package org.ysling.shopflow.wx.web;
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
import org.redisson.api.RateIntervalUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.core.redis.annotation.RequestRateLimiter;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.ysling.shopflow.wx.model.like.body.LikeSubmitBody;
import org.ysling.shopflow.wx.web.impl.WxWebLikeService;
import javax.validation.Valid;

/**
 * 用户点赞服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/like")
@Validated
public class WxLikeController {


    @Autowired
    private WxWebLikeService likeService;


    /**
     * 点赞
     * @param userId 用户ID
     * @param body   评论信息
     * @return 提交订单操作结果
     */
    @PostMapping("submit")
    @RequestRateLimiter(rate = 5, rateInterval = 10, timeUnit = RateIntervalUnit.SECONDS)
    public Object submit(@LoginUser String userId, @Valid @RequestBody LikeSubmitBody body) {
        return likeService.submit(userId , body);
    }
}