package org.ysling.litemall.wx.model.coupon.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponListBody extends PageBody implements Serializable {

    /**
     * 优惠券使用状态
     */
    private Short status;

}
