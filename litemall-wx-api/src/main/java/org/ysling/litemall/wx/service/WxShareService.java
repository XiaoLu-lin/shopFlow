package org.ysling.litemall.wx.service;
// Copyright (c) [ysling] [927069313@qq.com]
// [litemall-plus] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallShare;
import org.ysling.litemall.db.domain.LitemallUser;
import org.ysling.litemall.db.enums.ShareStatus;
import org.ysling.litemall.db.enums.UserLevel;
import org.ysling.litemall.db.service.impl.ShareServiceImpl;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分享记录服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_share")
public class WxShareService extends ShareServiceImpl {


    @Autowired
    private WxUserService userService;


    /**
     * 添加分享记录
     * @param user 用户
     * @param order 订单
     */
    public void addShare(LitemallUser user , LitemallOrder order){
        //分享逻辑处理
        if (user.getInviter() != null){
            LitemallUser inviterUser = userService.findById(user.getInviter());
            if (inviterUser != null){
                // 根据用户等级获取用户分享比例
                BigDecimal ratio = UserLevel.parseRatio(inviterUser.getUserLevel());
                // 商品总价 * 分享比例
                BigDecimal award = order.getGoodsPrice().multiply(ratio);
                LitemallShare share = new LitemallShare();
                share.setStatus(ShareStatus.STATUS_NONE.getStatus());
                share.setInviterId(inviterUser.getId());
                share.setOrderId(order.getId());
                share.setUserId(order.getUserId());
                share.setAward(award);
                add(share);
            }
        }
    }

    
    @Cacheable(sync = true)
    public Integer countAndShare(String inviterId) {
        QueryWrapper<LitemallShare> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallShare.INVITER_ID , inviterId);
        return Math.toIntExact(count(wrapper));
    }

    /**
     * 根据状态获取用户所有分享记录
     */
    @Cacheable(sync = true)
    public List<LitemallShare> queryInviterId(String inviterId , Short status) {
        QueryWrapper<LitemallShare> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallShare.INVITER_ID , inviterId);
        wrapper.eq(LitemallShare.STATUS , status);
        return queryAll(wrapper);
    }

    /**
     * 获取用户所有分享记录
     */
    @Cacheable(sync = true)
    public List<LitemallShare> queryInviterId(String inviterId) {
        QueryWrapper<LitemallShare> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallShare.INVITER_ID , inviterId);
        return queryAll(wrapper);
    }


}
