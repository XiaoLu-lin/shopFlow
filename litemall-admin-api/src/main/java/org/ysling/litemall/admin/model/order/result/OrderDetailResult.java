package org.ysling.litemall.admin.model.order.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallOrderGoods;
import org.ysling.litemall.db.entity.UserInfo;
import java.io.Serializable;
import java.util.List;

/**
 * 订单详情
 * @author Ysling
 */
@Data
public class OrderDetailResult implements Serializable {

    /**
     * 用户信息
     */
    private UserInfo user;
    /**
     * 订单状态文本
     */
    private String orderStatusText;
    /**
     * 订单信息
     */
    private LitemallOrder order;
    /**
     * 商品信息
     */
    private List<LitemallOrderGoods> orderGoods;



}
