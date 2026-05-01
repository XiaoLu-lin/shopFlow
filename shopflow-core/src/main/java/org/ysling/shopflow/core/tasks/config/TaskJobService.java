package org.ysling.shopflow.core.tasks.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.tasks.service.TaskService;
import org.ysling.shopflow.db.domain.ShopflowCoupon;
import org.ysling.shopflow.db.domain.ShopflowCouponUser;
import org.ysling.shopflow.db.domain.ShopflowGrouponRules;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.enums.*;
import org.ysling.shopflow.db.service.*;

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


    public List<ShopflowOrder> queryUnconfirmed() {
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.ORDER_STATUS , OrderStatus.STATUS_SHIP.getStatus());
        return orderService.queryAll(wrapper);
    }

    public List<ShopflowOrder> queryComment() {
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.ORDER_STATUS , OrderStatus.STATUS_AUTO_CONFIRM.getStatus());
        wrapper.gt(ShopflowOrder.COMMENTS , 0);
        return orderService.queryAll(wrapper);
    }

    public List<ShopflowOrder> queryUnpaid() {
        QueryWrapper<ShopflowOrder> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowOrder.ORDER_STATUS , OrderStatus.STATUS_CREATE.getStatus());
        return orderService.queryAll(wrapper);
    }

    public List<ShopflowGrouponRules> queryGrouponRulesExpired() {
        QueryWrapper<ShopflowGrouponRules> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGrouponRules.STATUS , GrouponRuleStatus.RULE_STATUS_ON.getStatus());
        return grouponRulesService.queryAll(wrapper);
    }

    /**
     * 查询过期的优惠券:
     * 注意：如果timeType=0, 即基于领取时间有效期的优惠券，则优惠券不会过期
     */
    public List<ShopflowCoupon> queryCouponExpired() {
        QueryWrapper<ShopflowCoupon> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCoupon.TIME_TYPE , CouponTimeType.TIME_TYPE_TIME);
        wrapper.eq(ShopflowCoupon.STATUS , CouponStatus.STATUS_NORMAL);
        wrapper.lt(ShopflowCoupon.END_TIME , LocalDateTime.now());
        return couponService.queryAll(wrapper);
    }

    public List<ShopflowCouponUser> queryCouponUserExpired() {
        QueryWrapper<ShopflowCouponUser> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCouponUser.STATUS , CouponUserStatus.STATUS_USABLE);
        wrapper.lt(ShopflowCouponUser.END_TIME , LocalDateTime.now());
        return couponUserService.queryAll(wrapper);
    }

}
