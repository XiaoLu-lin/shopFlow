package org.ysling.shopflow.wx.model.coupon.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.core.annotation.NotValue;
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
