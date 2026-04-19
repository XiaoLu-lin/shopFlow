package org.ysling.litemall.admin.model.coupon.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 优惠券使用列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponUserListBody extends PageBody {

    /**
     * 使用状态
     */
    private Short status;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 优惠券ID
     */
    private String couponId;



}
