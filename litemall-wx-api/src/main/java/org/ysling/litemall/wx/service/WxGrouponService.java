package org.ysling.litemall.wx.service;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallGroupon;
import org.ysling.litemall.db.domain.LitemallGrouponRules;
import org.ysling.litemall.db.enums.GrouponRuleStatus;
import org.ysling.litemall.db.enums.GrouponStatus;
import org.ysling.litemall.db.service.impl.GrouponServiceImpl;
import java.util.List;

/**
 * 团购服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_groupon")
public class WxGrouponService extends GrouponServiceImpl {


    @Autowired
    private WxGrouponRulesService grouponRulesService;


    /**
     * 验证团购是否有效
     * @return null
     */
    public Object isGroupon(String grouponId){
        LitemallGroupon groupon = findById(grouponId);
        if (groupon == null) {
            return ResponseUtil.badArgument();
        }
        //验证活动是否有效
        LitemallGrouponRules rules = grouponRulesService.findById(groupon.getRulesId());
        if (rules == null) {
            return ResponseUtil.badArgument();
        }

        //团购规则已经过期
        if (rules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_DOWN_EXPIRE.getStatus())) {
            return ResponseUtil.fail("团购已过期!");
        }
        //团购规则已经下线
        if (rules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_DOWN_ADMIN.getStatus())) {
            return ResponseUtil.fail( "团购已下线!");
        }
        //团购人数已满
        if(countGroupon(grouponId) >= (rules.getDiscountMember() - 1)){
            return ResponseUtil.fail( "团购活动人数已满!");
        }
        return null;
    }

    /**
     * 验证团购是否有效
     * @param grouponRulesId  团购规则id
     * @param grouponLinkId  团购参与id
     * @param userId 用户id
     * @return 团购状态
     */
    public Object isGroupon(String grouponRulesId , String grouponLinkId , String userId){
        //如果是团购项目,验证活动是否有效
        LitemallGrouponRules rules = grouponRulesService.findById(grouponRulesId);
        //找不到记录
        if (rules == null) {
            return ResponseUtil.badArgument();
        }
        //团购规则已经过期
        if (rules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_DOWN_EXPIRE.getStatus())) {
            return ResponseUtil.fail( "团购已过期!");
        }
        //团购规则已经下线
        if (rules.getStatus().equals(GrouponRuleStatus.RULE_STATUS_DOWN_ADMIN.getStatus())) {
            return ResponseUtil.fail("团购已下线!");
        }

        if (grouponLinkId != null && !grouponLinkId.equals("0")) {
            //团购人数已满
            if(countGroupon(grouponLinkId) >= (rules.getDiscountMember() - 1)){
                return ResponseUtil.fail( "团购活动人数已满!");
            }
            // TODO 这里业务方面允许用户多次开团，以及多次参团，
            // 但是会限制以下两点：
            // （1）不允许参加已经加入的团购
            if(hasJoin(userId, grouponLinkId)){
                return ResponseUtil.fail("团购活动已经参加!");
            }
            // （2）不允许参加自己开团的团购
            LitemallGroupon groupon = queryById(userId, grouponLinkId);
            if(groupon!=null) {
                if(groupon.getCreatorUserId().equals(userId)){
                    return ResponseUtil.fail("团购活动已经参加!");
                }
            }
        }
        return null;
    }


    /**
     * 添加团购信息
     * @param orderId  订单id
     * @param userId  用户id
     * @param grouponRulesId   团购规则id
     * @param grouponLinkId   团购参与id
     */
    public void addGroupon(String orderId, String userId, String grouponRulesId , String grouponLinkId){
        if (grouponRulesId != null && !"0".equals(grouponRulesId)) {
            LitemallGroupon groupon = new LitemallGroupon();
            groupon.setOrderId(orderId);
            groupon.setStatus(GrouponStatus.STATUS_NONE.getStatus());
            groupon.setUserId(userId);
            groupon.setRulesId(grouponRulesId);
            //参与者
            if (grouponLinkId != null && !"0".equals(grouponLinkId)) {
                //参与的团购记录
                LitemallGroupon baseGroupon = findById(grouponLinkId);
                groupon.setCreatorUserId(baseGroupon.getCreatorUserId());
                groupon.setShareUrl(baseGroupon.getShareUrl());
                groupon.setGrouponId(grouponLinkId);
                add(groupon);
            } else {
                groupon.setGrouponId("0");
                groupon.setCreatorUserId(userId);
                add(groupon);
            }
        }
    }


    /**
     * 获取某个团购活动参与的记录
     */
    @Cacheable(sync = true)
    public List<LitemallGroupon> queryJoinRecord(String grouponId) {
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.GROUPON_ID , grouponId);
        wrapper.eq(LitemallGroupon.STATUS , GrouponStatus.STATUS_ON.getStatus());
        wrapper.orderByDesc(LitemallGroupon.ADD_TIME);
        return queryAll(wrapper);
    }


    /**
     * 根据ID查询记录
     */
    @Cacheable(sync = true)
    public LitemallGroupon queryById(String userId, String id) {
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.USER_ID , userId);
        wrapper.eq(LitemallGroupon.ID , id);
        return getOne(wrapper , false);
    }

    @Cacheable(sync = true)
    public LitemallGroupon findByOrderId(String orderId) {
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.ORDER_ID , orderId);
        return getOne(wrapper , false);
    }


    /**
     * 返回某个发起的团购参与人数
     */
    @Cacheable(sync = true)
    public Integer countGroupon(String grouponId) {
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.GROUPON_ID , grouponId);
        wrapper.eq(LitemallGroupon.STATUS , GrouponStatus.STATUS_NONE.getStatus());
        return Math.toIntExact(count(wrapper));
    }

    @Cacheable(sync = true)
    public boolean hasJoin(String userId, String grouponId) {
        QueryWrapper<LitemallGroupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGroupon.USER_ID , userId);
        wrapper.eq(LitemallGroupon.GROUPON_ID , grouponId);
        wrapper.eq(LitemallGroupon.STATUS , GrouponStatus.STATUS_NONE.getStatus());
        return exists(wrapper);
    }

}
