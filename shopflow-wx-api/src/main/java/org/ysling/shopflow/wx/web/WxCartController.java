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
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.wx.model.cart.body.CartCheckedBody;
import org.ysling.shopflow.wx.model.cart.body.CartCheckoutBody;
import org.ysling.shopflow.wx.web.impl.WxWebCartService;
import javax.validation.Valid;
import java.util.*;

/**
 * 用户购物车服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/cart")
@Validated
public class WxCartController {

    @Autowired
    private WxWebCartService cartService;

    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("index")
    public Object index(@LoginUser String userId) {
        return cartService.index(userId);
    }

    /**
     * 加入商品到购物车
     * <p>
     * 如果已经存在购物车货品，则增加数量；
     * 否则添加新的购物车货品项。

     * @param userId 用户ID
     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 加入购物车操作结果
     */
    @PostMapping("add")
    public Object add(@LoginUser String userId, @Valid @RequestBody ShopflowCart cart) {
        return cartService.add(userId , cart);
    }

    /**
     * 立即购买
     * <p>
     * 和add方法的区别在于：
     * 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖
     * 2. 添加成功以后，前者的逻辑是返回当前购物车商品数量，这里的逻辑是返回对应购物车项的ID
     *
     * @param userId 用户ID
     * @param cart   购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 立即购买操作结果
     */
    @PostMapping("fast/add")
    public Object fastAdd(@LoginUser String userId, @Valid @RequestBody ShopflowCart cart) {
        return cartService.fastAdd(userId , cart);
    }

    /**
     * 修改购物车商品货品数量
     * @param userId 用户ID
     * @param cart   购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
     * @return 修改结果
     */
    @PostMapping("update")
    public Object update(@LoginUser String userId, @Valid @RequestBody ShopflowCart cart) {
        return cartService.update(userId , cart);
    }

    /**
     * 购物车商品货品勾选状态
     * @param userId 用户ID
     * @param body   购物车商品信息， { productIds: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
    @PostMapping("checked")
    public Object checked(@LoginUser String userId, @Valid @RequestBody CartCheckedBody body) {
        return cartService.checked(userId , body);
    }

    /**
     * 购物车商品删除
     * @param userId 用户ID
     * @param productIds   购物车商品信息， { productIds: xxx }
     * @return 购物车信息
     */
    @PostMapping("delete")
    public Object delete(@LoginUser String userId, @JsonBody List<String> productIds) {
        return cartService.delete(userId , productIds);
    }

    /**
     * 购物车商品货品数量
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("count")
    public Object goodsCount(@LoginUser(require = false) String userId) {
        return cartService.goodsCount(userId);
    }

    /**
     * 购物车下单
     */
    @GetMapping("checkout")
    public Object checkout(@LoginUser String userId, CartCheckoutBody body) {
        return cartService.checkout(userId , body);
    }


}
