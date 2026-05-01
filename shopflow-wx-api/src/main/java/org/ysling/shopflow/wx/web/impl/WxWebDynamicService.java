package org.ysling.shopflow.wx.web.impl;
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

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.jobs.ApiJob;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.weixin.service.WxSecCheckService;
import org.ysling.shopflow.db.domain.ShopflowDynamic;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.enums.LikeType;
import org.ysling.shopflow.wx.model.dynamic.body.DynamicListBody;
import org.ysling.shopflow.wx.model.dynamic.result.DynamicListResult;
import org.ysling.shopflow.wx.service.WxDynamicService;
import org.ysling.shopflow.wx.service.WxLikeService;
import org.ysling.shopflow.wx.service.WxUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 动态信息
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebDynamicService {

    @Autowired
    private WxDynamicService dynamicService;
    @Autowired
    private WxUserService userService;
    @Autowired
    private WxLikeService likeService;
    @Autowired
    private WxSecCheckService secCheckService;

    /**
     * 动态列表
     */
    public Object list(String userId, DynamicListBody body) {
        List<ShopflowDynamic> dynamicList = dynamicService.querySelective(body);
        ArrayList<DynamicListResult> resultList = new ArrayList<>();
        for (ShopflowDynamic dynamic : dynamicList) {
            //给查寻出来的时间加上浏览量
            dynamic.setLookCount(dynamic.getLookCount() + 1);
            dynamicService.updateSelective(dynamic);
            //动态信息
            DynamicListResult result = new DynamicListResult();
            BeanUtil.copyProperties(dynamic , result);
            result.setDynamicLike(likeService.count(LikeType.TYPE_TIMELINE, dynamic.getId(), userId));
            //用户信息
            ShopflowUser user = userService.findById(dynamic.getUserId());
            if (user == null){
                if (ApiJob.USER_ID.equals(dynamic.getUserId())){
                    result.setNickName("每日段子");
                    result.setAvatarUrl("https://th.bing.com/th?id=OSK.2fe5b3f3f141834f896fe8a9ffe3a1dc&w=148&h=148&c=7&o=6&dpr=1.8&pid=SANGAM");
                } else {
                    continue;
                }
            } else {
                result.setNickName(user.getNickName());
                result.setAvatarUrl(user.getAvatarUrl());
            }
            resultList.add(result);
        }
        return ResponseUtil.okList(resultList, dynamicList);
    }


    /**
     * 发布日常
     *
     * @param userId   用户ID
     * @param dynamic 动态发布信息
     * @return 操作结果
     */
    public Object submit(String userId, ShopflowDynamic dynamic) {
        String content = dynamic.getContent();
        if (Objects.isNull(content)) {
            return ResponseUtil.badArgument();
        }
        //文本校验
        ShopflowUser user = userService.findById(userId);
        secCheckService.checkMessage(user.getOpenid(), dynamic.toString());
        //添加动态
        dynamic.setUserId(userId);
        dynamicService.add(dynamic);
        return ResponseUtil.ok();
    }

    /**
     * 删除日常
     *
     * @param userId   用户ID
     * @param timeVoId 动态发布信息的id
     * @return 操作结果
     */
    public Object delete(String userId, String timeVoId) {
        ShopflowDynamic dynamic = dynamicService.findById(timeVoId);
        if (dynamic == null){
            return ResponseUtil.badArgument();
        }
        if (StpUtil.isLogin()){
            dynamicService.deleteById(timeVoId);
        }else if(userId.equals(dynamic.getUserId())){
            dynamicService.deleteById(timeVoId);
        }else {
            return ResponseUtil.fail(600,"无权限");
        }
        return ResponseUtil.ok();
    }


}
