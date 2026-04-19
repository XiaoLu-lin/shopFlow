package org.ysling.litemall.core.utils;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import org.ysling.litemall.db.domain.LitemallAftersale;
import org.ysling.litemall.db.domain.LitemallOrder;
import org.ysling.litemall.db.domain.LitemallOrderGoods;

import java.time.LocalDateTime;
import java.util.List;

public class NotifyMessageUtil {

    public static String orderMessage(LitemallOrder order , LitemallOrderGoods orderGoods){
        StringBuilder inform = new StringBuilder("<html><head></head><body>");
        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<thead>");
        inform.append("<th style='padding:15px;'>" + "订单详情" + "</th>");
        inform.append("</thead>");

        inform.append("</table>");
        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;width: 65px;'>" + "订单编号:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getOrderSn() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "收货人:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getConsignee() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "联系电话:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getMobile() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "收货地址:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getAddress() + "</td>");
        inform.append("</tr>");

        String [] getSpecifications = orderGoods.getSpecifications();

        StringBuffer specifications = new StringBuffer();
        for (int i = 0; i < getSpecifications.length; i++) {
            specifications.append("["+getSpecifications[i]+"] ");
        }
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + "商品名称:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + orderGoods.getGoodsName() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品规格:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + specifications.toString() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品数量:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "x"+orderGoods.getNumber() + "</td>");
        inform.append("</tr>");

        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + "总付款:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + order.getActualPrice()+"元" + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "用户留言:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getMessage() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "下单时间" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getPayTime() + "</td>");
        inform.append("</tr>");
        inform.append("</table>");

        inform.append("<p>");
        inform.append("<a href=\"http://litemall-plus.ysling.site\" style='font-size: 16px; line-height: 45px; display: block; background-color: rgb(0, 164, 255); color: rgb(255, 255, 255); text-align: center; text-decoration: none; margin-top: 20px; border-radius: 3px;'>去发货</a>");
        inform.append("</p>");

        inform.append("</body></html>");
        return inform.toString();
    }


    public static String refundMessage(LitemallOrder order , LitemallOrderGoods orderGoods){
        StringBuilder inform = new StringBuilder("<html><head></head><body>");
        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<thead>");
        inform.append("<th style='padding:15px;'>" + "退款详情" + "</th>");
        inform.append("</thead>");
        inform.append("</table>");

        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;width: 65px;'>" + "订单编号:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getOrderSn() + "</td>");
        inform.append("</tr>");

        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "退款用户:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getConsignee() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "联系电话:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getMobile() + "</td>");
        inform.append("</tr>");

        String [] getSpecifications = orderGoods.getSpecifications();

        StringBuffer specifications = new StringBuffer();
        for (int i = 0; i < getSpecifications.length; i++) {
            specifications.append("["+getSpecifications[i]+"] ");
        }
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + "商品名称:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + orderGoods.getGoodsName() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品规格:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + specifications.toString() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品数量:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "x"+orderGoods.getNumber() + "</td>");
        inform.append("</tr>");

        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + "退款金额:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + order.getActualPrice()+"元" + "</td>");
        inform.append("</tr>");
        inform.append("</table>");

        inform.append("<p>");
        inform.append("<a href=\"http://litemall-plus.ysling.site\" style='font-size: 16px; line-height: 45px; display: block; background-color: rgb(0, 164, 255); color: rgb(255, 255, 255); text-align: center; text-decoration: none; margin-top: 20px; border-radius: 3px;'>去查看</a>");
        inform.append("</p>");

        inform.append("</body></html>");
        return inform.toString();
    }


    /**
     * 售后通知模板
     * @param order
     * @param orderGoods
     * @param aftersale
     * @return
     */
    public static String aftersaleMessage(LitemallOrder order ,LitemallOrderGoods orderGoods, LitemallAftersale aftersale){
        StringBuilder inform = new StringBuilder("<html><head></head><body>");
        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<thead>");
        inform.append("<th style='padding:15px;'>" + "售后详情" + "</th>");
        inform.append("</thead>");
        inform.append("</table>");

        inform.append("<table border='1' style='font-size:15px;width:100%;'>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;width: 65px;'>" + "订单编号:" + "</td>");
        inform.append("<td style='padding:15px;'>" + order.getOrderSn() + "</td>");
        inform.append("</tr>");
        if(!"".equals(order.getConsignee()) && !"无".equals(order.getConsignee())){
            inform.append("<tr>");
            inform.append("<td style='padding:15px;'>" + "退款用户:" + "</td>");
            inform.append("<td style='padding:15px;'>" + order.getConsignee() + "</td>");
            inform.append("</tr>");
            inform.append("<tr>");
            inform.append("<td style='padding:15px;'>" + "联系电话:" + "</td>");
            inform.append("<td style='padding:15px;'>" + order.getMobile() + "</td>");
            inform.append("</tr>");
        }

        String [] getSpecifications = orderGoods.getSpecifications();
        StringBuffer specifications = new StringBuffer();
        for (int i = 0; i < getSpecifications.length; i++) {
            specifications.append("["+getSpecifications[i]+"] ");
        }
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + "商品名称:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + orderGoods.getGoodsName() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品规格:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + specifications + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品数量:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "x"+orderGoods.getNumber() + "</td>");
        inform.append("</tr>");

        inform.append("<tr>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + "退款金额:" + "</td>");
        inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + aftersale.getAmount()+"元" + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "退款类型:" + "</td>");
        if (aftersale.getType() == 0){
            inform.append("<td style='padding:15px;'>" + "未收货退款" + "</td>");
        }else if (aftersale.getType() == 1){
            inform.append("<td style='padding:15px;'>" + "已收货（无需退货）" + "</td>");
        }else if (aftersale.getType() == 2){
            inform.append("<td style='padding:15px;'>" + "退货退款" + "</td>");
        }
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "退款备注:" + "</td>");
        inform.append("<td style='padding:15px;'>" + aftersale.getReason() + "</td>");
        inform.append("</tr>");
        inform.append("<tr>");
        inform.append("<td style='padding:15px;'>" + "申请日期:" + "</td>");
        inform.append("<td style='padding:15px;'>" + LocalDateTime.now() + "</td>");
        inform.append("</tr>");
        inform.append("</table>");

        inform.append("<p>");
        inform.append("<a href=\"http://www.makbk.com\" style='font-size: 16px; line-height: 45px; display: block; background-color: rgb(0, 164, 255); color: rgb(255, 255, 255); text-align: center; text-decoration: none; margin-top: 20px; border-radius: 3px;'>去查看</a>");
        inform.append("</p>");

        inform.append("</body></html>");

        return inform.toString();
    }
}
