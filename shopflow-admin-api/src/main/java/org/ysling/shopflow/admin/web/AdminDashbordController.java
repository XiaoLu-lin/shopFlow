package org.ysling.shopflow.admin.web;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.model.dashbord.result.DashBordInfoResult;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.admin.service.AdminGoodsProductService;
import org.ysling.shopflow.admin.service.AdminGoodsService;
import org.ysling.shopflow.admin.service.AdminOrderService;
import org.ysling.shopflow.admin.service.AdminUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 首页仪表盘
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@Validated
public class AdminDashbordController {

    @Autowired
    private AdminUserService userService;
    @Autowired
    private AdminGoodsService goodsService;
    @Autowired
    private AdminOrderService orderService;
    @Autowired
    private AdminGoodsProductService productService;


    /**
     * 首页仪表盘
     */
    @GetMapping("")
    public Object info() {
        DashBordInfoResult result = new DashBordInfoResult();
        result.setUserTotal(userService.count());
        result.setGoodsTotal(goodsService.count());
        result.setProductTotal(productService.count());
        result.setOrderTotal(orderService.count());
        return ResponseUtil.ok(result);
    }


}
