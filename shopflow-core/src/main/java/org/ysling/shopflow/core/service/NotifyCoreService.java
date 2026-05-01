package org.ysling.shopflow.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.notify.service.NotifyMailService;
import org.ysling.shopflow.core.utils.NotifyMessageUtil;
import org.ysling.shopflow.core.weixin.service.SubscribeMessageService;
import org.ysling.shopflow.db.service.IBrandService;
import org.ysling.shopflow.db.service.IUserService;
import org.ysling.shopflow.db.domain.*;
import java.util.List;

/**
 * @author Ysling
 */
@Service
public class NotifyCoreService {

    @Autowired
    private IUserService userService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;
    @Autowired
    private NotifyMailService mailService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private CommonService commonService;

    /**
     * 订单消息通知
     * @param order 订单
     */
    public void orderNotify(ShopflowOrder order){
        //获取订单商品
        List<ShopflowOrderGoods> orderGoodsList = commonService.queryByOid(order.getId());
        for (ShopflowOrderGoods orderGoods :orderGoodsList) {
            ShopflowBrand brand = brandService.findById(order.getBrandId());
            //新订单订阅通知
            ShopflowUser user = userService.findById(brand.getUserId());
            if (brand.getUserId() != null && user != null){
                subscribeMessageService.newOrderSubscribe(user.getOpenid(),order,orderGoods);
            }
            //店铺邮箱通知
            String orderMessage = NotifyMessageUtil.orderMessage(order, orderGoods);
            if (StringUtils.hasText(brand.getMail())){
                mailService.notifyMail("新订单通知", orderMessage,brand.getMail());
            }else {
                mailService.notifyMail("新订单通知", orderMessage);
            }
        }
    }



    /**
     * 订单退款通知
     * @param order 订单
     */
    public void orderRefundNotify(ShopflowOrder order){
        List<ShopflowOrderGoods> orderGoodsList = commonService.queryByOid(order.getId());
        for (ShopflowOrderGoods orderGoods :orderGoodsList) {
            String refundMessage = NotifyMessageUtil.refundMessage(order, orderGoods);
            ShopflowBrand brand = brandService.findById(order.getBrandId());
            if (StringUtils.hasText(brand.getMail())){
                mailService.notifyMail("退款申请", refundMessage, brand.getMail());
            }else {
                mailService.notifyMail("退款申请", refundMessage);
            }
        }
    }

    /**
     * 订单售后通知
     * @param order 订单
     */
    public void aftersaleRefundNotify(ShopflowOrder order, ShopflowAftersale aftersale){
        ShopflowBrand brand = brandService.findById(order.getBrandId());
        ShopflowOrderGoods orderGoods = commonService.findByOrderId(order.getId());
        String message = NotifyMessageUtil.aftersaleMessage(order, orderGoods, aftersale);
        if (StringUtils.hasText(brand.getMail())){
            mailService.notifyMail("售后申请", message,brand.getMail());
        }else {
            mailService.notifyMail("售后申请", message);
        }
    }



}
