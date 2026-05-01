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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.ysling.shopflow.db.service.impl.GoodsServiceImpl;
import org.ysling.shopflow.wx.model.brand.result.BrandGoodsListBody;
import org.ysling.shopflow.wx.model.goods.body.GoodsListBody;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_goods")
public class WxGoodsService extends GoodsServiceImpl {
    
    /**
     * 获取热卖商品已上架
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByHot(Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.IS_HOT , true);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 获取新品上市已上架
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByNew(Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.IS_NEW , true);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 获取团购商品已上架
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByGroupon(Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.IS_GROUPON , true);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 分页获取所有已上架商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByAll(Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取店铺下已上架的商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByBrand(String brandId, Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.BRAND_ID , brandId);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取店铺下的商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByBrand(BrandGoodsListBody body) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(body);
        wrapper.eq(ShopflowGoods.BRAND_ID , body.getBrandId());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取分类下已上架的商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByCategory(String catId, Integer limit) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(ShopflowGoods.CATEGORY_ID , catId);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 小程序搜索商品信息
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> querySelective(GoodsListBody body) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(body);
        if (body.getCategoryId() != null && !body.getCategoryId().equals("0")) {
            wrapper.eq(ShopflowGoods.CATEGORY_ID , body.getCategoryId());
        }
        if (body.getBrandId() != null) {
            wrapper.eq(ShopflowGoods.BRAND_ID , body.getBrandId());
        }
        if (body.getIsNew() != null) {
            wrapper.eq(ShopflowGoods.IS_NEW , body.getIsNew());
        }
        if (body.getIsHot() != null) {
            wrapper.eq(ShopflowGoods.IS_HOT , body.getIsHot());
        }
        if (body.getKeyword() != null) {
            wrapper.like(ShopflowGoods.KEYWORDS , body.getKeyword())
                    .or().like(ShopflowGoods.NAME , body.getKeyword());
        }
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        if (body.getIsAdmin() != null && body.getIsAdmin()) {
            return queryAll(wrapper);
        } else {
            wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        }
        return queryAll(wrapper);
    }

    /**
     * 获取某个商品信息，已上架商品，仅展示相关内容
     */
    @Cacheable(sync = true)
    public ShopflowGoods findByIdVO(String id) {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoods.ID , id);
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return getOne(wrapper);
    }


    /**
     * 获取所有在售物品总数
     */
    @Cacheable(sync = true)
    public Integer queryOnSale() {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return Math.toIntExact(count(wrapper));
    }


    /**
     * 按分类获取已上架商品
     */
    @Cacheable(sync = true)
    public List<String> getCatIds(String brandId, String keywords, Boolean isHot, Boolean isNew) {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        if (!Objects.isNull(brandId)) {
            wrapper.eq(ShopflowGoods.BRAND_ID , brandId);
        }
        if (!Objects.isNull(isNew)) {
            wrapper.eq(ShopflowGoods.IS_NEW , isNew);
        }
        if (!Objects.isNull(isHot)) {
            wrapper.eq(ShopflowGoods.IS_HOT , isHot);
        }
        if (!Objects.isNull(keywords)) {
            wrapper.like(ShopflowGoods.KEYWORDS , keywords);
            wrapper.like(ShopflowGoods.NAME , keywords);
        }
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        List<ShopflowGoods> goodsList = queryAll(wrapper);
        List<String> cats = new ArrayList<>();
        for (ShopflowGoods goods : goodsList) {
            cats.add(goods.getCategoryId());
        }
        return cats;
    }



    /**
     * 更具商品ID列表查询商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByIds(String[] ids) {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        wrapper.in(ShopflowGoods.ID , Arrays.asList(ids));
        wrapper.eq(ShopflowGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


}
