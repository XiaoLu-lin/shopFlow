package org.ysling.shopflow.wx.web;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RateIntervalUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.redis.annotation.RequestRateLimiter;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.wx.model.order.body.*;
import org.ysling.shopflow.wx.web.impl.WxWebOrderService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.util.List;

/**
 * 订单服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/order")
@Validated
public class WxOrderController {

    @Autowired
    private WxWebOrderService orderService;

    /**
     * 订单列表
     */
    @GetMapping("list")
    public Object list(@LoginUser String userId, OrderListBody body) {
        return orderService.list(userId, body);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("detail")
    public Object detail(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.detail(userId, orderId);
    }

    /**
     * 提交订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
     * @return 提交订单操作结果
     */
    @PostMapping("submit")
    @RequestRateLimiter(rate = 1, rateInterval = 3, timeUnit = RateIntervalUnit.SECONDS , errMsg = "你有一笔相同订单已提交，请等待")
    public Object submit(@LoginUser String userId, @Valid @RequestBody OrderSubmitBody body) {
        return orderService.submit(userId, body);
    }


    /**
     * 取消订单
     *
     * @param userId    用户ID
     * @param orderId   订单ID
     * @return 取消订单操作结果
     */
    @PostMapping("cancel")
    public Object cancel(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.cancel(userId, orderId);
    }

    /**
     * 付款订单的预支付会话标识
     *
     * @param userId    用户ID
     * @param orderIds  订单ID列表
     * @return 支付订单ID
     */
    @PostMapping("prepay")
    public Object prepay(@LoginUser String userId, @JsonBody List<String> orderIds, HttpServletRequest request) {
        return orderService.prepay(userId, orderIds, request);
    }

    /**
     * 微信付款成功或失败回调接口
     * <p>
     *  TODO
     *  注意，这里pay-notify是示例地址，建议开发者应该设立一个隐蔽的回调地址
     *
     * @param request 请求内容
     * @return 操作结果
     */
    @PostMapping("pay-notify")
    public Object payNotify(HttpServletRequest request) {
        return orderService.payNotify(request);
    }


    /**
     * 订单申请退款
     *
     * @param userId 用户ID
     * @param orderId   订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @PostMapping("refund")
    public Object refund(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.refund(userId, orderId);
    }

    /**
     * 确认收货
     *
     * @param userId 用户ID
     * @param orderId   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("confirm")
    public Object confirm(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.confirm(userId, orderId);
    }

    /**
     * 删除订单
     *
     * @param userId 用户ID
     * @param orderId   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("delete")
    public Object delete(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.delete(userId, orderId);
    }

    /**
     * 待评价订单商品信息
     *
     * @param userId  用户ID
     * @param goodsId 订单商品ID
     * @return 待评价订单商品信息
     */
    @GetMapping("goods")
    public Object goods(@LoginUser String userId, @JsonBody String goodsId) {
        return orderService.goods(userId, goodsId);
    }

    /**
     * 评价订单商品
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PostMapping("comment")
    public Object comment(@LoginUser String userId, @Valid @RequestBody OrderCommentBody body) {
        return orderService.comment(userId, body);
    }

    /**
     * 订单退款
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @PostMapping("admin/refund")
    public Object adminRefund(@LoginUser String userId, @Valid @RequestBody OrderAdminRefundBody body) {
        return orderService.adminRefund(userId , body);
    }

    /**
     * 商家取消订单
     *
     * @param orderId 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @PostMapping("admin/cancel")
    public Object adminCancel(@LoginUser String userId, @JsonBody String orderId) {
        return orderService.adminCancel(userId, orderId);
    }

    /**
     * 发货
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @PostMapping("admin/ship")
    public Object adminShip(@LoginUser String userId, @Valid @RequestBody OrderAdminShipBody body) {
        return orderService.adminShip(userId , body);
    }


}