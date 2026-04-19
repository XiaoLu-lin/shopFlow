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

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.service.RewardCoreService;
import org.ysling.litemall.core.system.enums.SystemMallConfig;
import org.ysling.litemall.core.system.enums.SystemWxConfig;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.core.utils.Inheritable.Inheritable;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.*;
import org.ysling.litemall.wx.model.home.body.HomeNavigateBody;
import org.ysling.litemall.wx.model.home.result.HomeAboutResult;
import org.ysling.litemall.wx.model.home.result.HomeIndexResult;
import org.ysling.litemall.wx.service.*;
import java.util.List;
import java.util.Objects;

/**
 * 首页服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebHomeService {


    @Autowired
    private WxAdService adService;
    @Autowired
    private WxGoodsService goodsService;
    @Autowired
    private WxCategoryService categoryService;
    @Autowired
    private WxGrouponService grouponService;
    @Autowired
    private WxGrouponRulesService grouponRulesService;
    @Autowired
    private WxCouponService couponService;
    @Autowired
    private WxRewardService rewardService;
    @Autowired
    private RewardCoreService rewardCoreService;
    @Autowired
    private WxTenantService tenantService;
    @Autowired
    private WxRewardTaskService rewardTaskService;


    /**
     * 小程序授权
     * @return 商城介绍信息
     */
    public Object auth(String appid) {
        return ResponseUtil.ok(tenantService.getTenant(appid));
    }

    /**
     * 首页数据
     * @param userId 当用户已经登录时，非空。未登录状态为null
     * @return 首页数据
     */
    public Object index(String userId, String appid) {
        String tenantToken = tenantService.getTenant(appid);

        // 广告列表
        Inheritable<List<LitemallAd>> bannerTask = new Inheritable<>(
                () -> adService.queryIndex()
        );

        // 分类列表
        Inheritable<List<LitemallCategory>> channelTask = new Inheritable<>(
                () -> categoryService.queryChannel()
        );

        // 优惠券列表
        Inheritable<List<LitemallCoupon>> couponListTask = new Inheritable<>(
                () -> couponService.queryAvailableList(userId, SystemWxConfig.getCouponLimit())
        );

        // 新品首发
        Inheritable<List<LitemallGoods>> newGoodsListTask = new Inheritable<>(
                () -> goodsService.queryByNew(SystemWxConfig.getNewLimit())
        );

        // 热卖专区
        Inheritable<List<LitemallGoods>> hotGoodsListTask = new Inheritable<>(
                () -> goodsService.queryByHot(SystemWxConfig.getHotLimit())
        );

        // 赏金任务
        Inheritable<List<LitemallRewardTask>> rewardTaskListTask = new Inheritable<>(
                () -> rewardTaskService.queryByReward(SystemWxConfig.getRewardLimit())
        );

        // 团购专区
        Inheritable<List<LitemallGrouponRules>> grouponRuleListTask = new Inheritable<>(
                () -> grouponRulesService.queryByGroupon(SystemWxConfig.getGrouponLimit())
        );

        // 首页商品分页
        Inheritable<PageInfo<LitemallGoods>> allGoodsListTask = new Inheritable<>(
                () -> PageInfo.of(goodsService.queryByAll(SystemWxConfig.getAllLimit()))
        );

        HomeIndexResult result = new HomeIndexResult();
        try {
            result.setTenantId(tenantToken);
            result.setBanner(bannerTask.get());
            result.setChannel(channelTask.get());
            result.setCouponList(couponListTask.get());
            result.setNewGoodsList(newGoodsListTask.get());
            result.setHotGoodsList(hotGoodsListTask.get());
            result.setAllGoodsList(allGoodsListTask.get());
            result.setRewardTaskList(rewardTaskListTask.get());
            result.setGrouponRuleList(grouponRuleListTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtil.ok(result);
    }



    /**
     * 判断首页初始化参数是否支持跳转
     */
    public Object isNavigate(HomeNavigateBody body) {
        String sceneId = body.getSceneId();
        String sceneType = body.getSceneType();
        if (Objects.equals(sceneId, "0")){
            return ResponseUtil.fail(800,"分享获取失败");
        }
        if (Objects.equals(sceneType,"goodsId")){
            LitemallGoods goods = goodsService.findById(sceneId);
            if (goods == null){
                return ResponseUtil.fail(800,"商品不存在");
            }
        }
        if (Objects.equals(sceneType,"grouponId")){
            Object groupon = grouponService.isGroupon(sceneId);
            if (groupon != null){
                return groupon;
            }
        }
        if (Objects.equals(sceneType,"rewardId")){
            LitemallReward reward = rewardService.findById(sceneId);
            if (reward == null) {
                return ResponseUtil.fail(800,"活动不存在");
            }
            Object serviceReward = rewardCoreService.isReward(reward.getTaskId());
            if (serviceReward != null){
                return serviceReward;
            }
        }
        return ResponseUtil.ok();
    }


    /**
     * 商城介绍信息
     * @return 商城介绍信息
     */
    public Object about() {
        HomeAboutResult result = new HomeAboutResult();
        result.setName(SystemMallConfig.getMallName());
        result.setPhone(SystemMallConfig.getMallPhone());
        result.setQq(SystemMallConfig.getMallQQ());
        result.setLongitude(SystemMallConfig.getMallLongitude());
        result.setLatitude(SystemMallConfig.getMallLatitude());
        String tenantId = TenantContextHolder.getLocalTenantId();
        LitemallTenant tenant = tenantService.findById(tenantId);
        if (tenant != null){
            result.setAddress(tenant.getAddress());
        }
        return ResponseUtil.ok(result);
    }

}