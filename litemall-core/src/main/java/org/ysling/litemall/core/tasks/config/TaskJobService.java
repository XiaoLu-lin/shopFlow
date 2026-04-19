package org.ysling.litemall.core.tasks.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.tasks.service.TaskService;
import org.ysling.litemall.db.domain.LitemallCoupon;
import org.ysling.litemall.db.domain.LitemallCouponUser;
import org.ysling.litemall.db.domain.LitemallGrouponRules;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.enums.*;
import org.ysling.litemall.db.service.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ysling
 */
@Service
public class TaskJobService {

    @Autowired
    private IGrouponRulesService grouponRulesService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private ICouponUserService couponUserService;


    public List<LitemallOrder> queryUnconfirmed() {
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.ORDER_STATUS , OrderStatus.STATUS_SHIP.getStatus());
        return orderService.queryAll(wrapper);
    }

    public List<LitemallOrder> queryComment() {
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.ORDER_STATUS , OrderStatus.STATUS_AUTO_CONFIRM.getStatus());
        wrapper.gt(LitemallOrder.COMMENTS , 0);
        return orderService.queryAll(wrapper);
    }

    public List<LitemallOrder> queryUnpaid() {
        QueryWrapper<LitemallOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallOrder.ORDER_STATUS , OrderStatus.STATUS_CREATE.getStatus());
        return orderService.queryAll(wrapper);
    }

    public List<LitemallGrouponRules> queryGrouponRulesExpired() {
        QueryWrapper<LitemallGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return grouponRulesService.queryAll(wrapper);
    }

    /**
     * 查询过期的优惠券:
     * 注意：如果timeType=0, 即基于领取时间有效期的优惠券，则优惠券不会过期
     */
    public List<LitemallCoupon> queryCouponExpired() {
        QueryWrapper<LitemallCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCoupon.TIME_TYPE , CouponTimeType.TIME_TYPE_TIME);
        wrapper.eq(LitemallCoupon.STATUS , CouponStatus.STATUS_NORMAL);
        wrapper.lt(LitemallCoupon.END_TIME , LocalDateTime.now());
        return couponService.queryAll(wrapper);
    }

    public List<LitemallCouponUser> queryCouponUserExpired() {
        QueryWrapper<LitemallCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallCouponUser.STATUS , CouponUserStatus.STATUS_USABLE);
        wrapper.lt(LitemallCouponUser.END_TIME , LocalDateTime.now());
        return couponUserService.queryAll(wrapper);
    }

}
