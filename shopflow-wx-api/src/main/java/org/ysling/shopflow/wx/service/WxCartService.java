package org.ysling.shopflow.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowCart;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.domain.ShopflowGoodsProduct;
import org.ysling.shopflow.db.service.impl.CartServiceImpl;

import java.util.*;

/**
 * 购物车服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_cart")
public class WxCartService extends CartServiceImpl {

    
    @Cacheable(sync = true)
    public ShopflowCart queryExist(String goodsId, String productId, String userId) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.GOODS_ID , goodsId);
        wrapper.eq(ShopflowCart.PRODUCT_ID , productId);
        wrapper.eq(ShopflowCart.USER_ID , userId);
        return getOne(wrapper);
    }


    /**
     * 添加到购物车
     */
    @CacheEvict(allEntries = true)
    public boolean addCart(String userId, ShopflowCart cart, ShopflowGoods goods, ShopflowGoodsProduct product) {
        if (product == null || cart.getNumber() > product.getNumber()) {
            return true;
        }
        cart.setUserId(userId);
        cart.setBrandId(goods.getBrandId());
        cart.setGoodsSn(goods.getGoodsSn());
        cart.setGoodsName((goods.getName()));
        cart.setAddress(goods.getAddress());
        cart.setIsTakeTheir(goods.getIsTakeTheir());
        cart.setPrice(product.getPrice());
        cart.setPicUrl(product.getUrl());
        cart.setSpecifications(product.getSpecifications());
        return !saveOrUpdate(cart);
    }

    
    @Cacheable(sync = true)
    public List<ShopflowCart> queryByUid(String userId) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.USER_ID , userId);
        return queryAll(wrapper);
    }


    
    @Cacheable(sync = true)
    public List<ShopflowCart> queryByUidAndChecked(String userId) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.USER_ID , userId);
        wrapper.eq(ShopflowCart.CHECKED , true);
        return queryAll(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public boolean delete(List<String> productIdList, String userId) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.USER_ID , userId);
        wrapper.in(ShopflowCart.PRODUCT_ID , productIdList);
        return remove(wrapper);
    }
    
    @Cacheable(sync = true)
    public ShopflowCart findById(String userId, String id) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.USER_ID , userId);
        wrapper.in(ShopflowCart.ID , id);
        return getOne(wrapper);
    }

    
    @CacheEvict(allEntries = true)
    public void updateCheck(String userId, List<String> idsList, Boolean checked) {
        QueryWrapper<ShopflowCart> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowCart.USER_ID , userId);
        wrapper.in(ShopflowCart.PRODUCT_ID , idsList);
        ShopflowCart cart = new ShopflowCart();
        cart.setChecked(checked);
        update(cart, wrapper);
    }

    
    /**
     * 获取选择商品
     */
    @Cacheable(sync = true)
    public List<ShopflowCart> getCheckedGoods(String userId, String cartId){
        if (cartId == null || cartId.equals("0")) {
            return queryByUidAndChecked(userId);
        } else {
            ShopflowCart cart = findById(userId, cartId);
            if (cart == null) {
                return null;
            }
            return Collections.singletonList(cart);
        }
    }

}
