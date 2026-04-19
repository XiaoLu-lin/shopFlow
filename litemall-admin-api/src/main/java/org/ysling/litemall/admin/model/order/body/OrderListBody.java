package org.ysling.litemall.admin.model.order.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.ysling.litemall.db.entity.PageBody;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderListBody extends PageBody {

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 订单编号
     */
    private String orderSn;
    /**
     * 店铺ID
     */
    private String brandId;
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 查询类型
     */
    private Integer showType;
    /**
     * 物流单号
     */
    private String shipSn;
    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    /**
     * 截至日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    /**
     * 订单状态列表
     */
    private List<Short> orderStatusArray;



}
