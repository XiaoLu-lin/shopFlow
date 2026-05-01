package org.ysling.shopflow.admin.model.order.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.domain.ShopflowOrderGoods;
import org.ysling.shopflow.db.entity.UserInfo;
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
    private ShopflowOrder order;
    /**
     * 商品信息
     */
    private List<ShopflowOrderGoods> orderGoods;



}
