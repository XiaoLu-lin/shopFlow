package org.ysling.litemall.wx.service;
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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.db.enums.GoodsStatus;
import org.ysling.litemall.db.service.impl.GoodsServiceImpl;
import org.ysling.litemall.wx.model.brand.result.BrandGoodsListBody;
import org.ysling.litemall.wx.model.goods.body.GoodsListBody;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods")
public class WxGoodsService extends GoodsServiceImpl {
    
    /**
     * 获取热卖商品已上架
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByHot(Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.IS_HOT , true);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 获取新品上市已上架
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByNew(Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.IS_NEW , true);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 获取团购商品已上架
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByGroupon(Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.IS_GROUPON , true);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }

    /**
     * 分页获取所有已上架商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByAll(Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取店铺下已上架的商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByBrand(String brandId, Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.BRAND_ID , brandId);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取店铺下的商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByBrand(BrandGoodsListBody body) {
        QueryWrapper<LitemallGoods> wrapper = startPage(body);
        wrapper.eq(LitemallGoods.BRAND_ID , body.getBrandId());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 获取分类下已上架的商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByCategory(String catId, Integer limit) {
        QueryWrapper<LitemallGoods> wrapper = startPage(new PageBody(limit));
        wrapper.eq(LitemallGoods.CATEGORY_ID , catId);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 小程序搜索商品信息
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> querySelective(GoodsListBody body) {
        QueryWrapper<LitemallGoods> wrapper = startPage(body);
        if (body.getCategoryId() != null && !body.getCategoryId().equals("0")) {
            wrapper.eq(LitemallGoods.CATEGORY_ID , body.getCategoryId());
        }
        if (body.getBrandId() != null) {
            wrapper.eq(LitemallGoods.BRAND_ID , body.getBrandId());
        }
        if (body.getIsNew() != null) {
            wrapper.eq(LitemallGoods.IS_NEW , body.getIsNew());
        }
        if (body.getIsHot() != null) {
            wrapper.eq(LitemallGoods.IS_HOT , body.getIsHot());
        }
        if (body.getKeyword() != null) {
            wrapper.like(LitemallGoods.KEYWORDS , body.getKeyword())
                    .or().like(LitemallGoods.NAME , body.getKeyword());
        }
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        if (body.getIsAdmin() != null && body.getIsAdmin()) {
            return queryAll(wrapper);
        } else {
            wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        }
        return queryAll(wrapper);
    }

    /**
     * 获取某个商品信息，已上架商品，仅展示相关内容
     */
    @Cacheable(sync = true)
    public LitemallGoods findByIdVO(String id) {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoods.ID , id);
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return getOne(wrapper);
    }


    /**
     * 获取所有在售物品总数
     */
    @Cacheable(sync = true)
    public Integer queryOnSale() {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        return Math.toIntExact(count(wrapper));
    }


    /**
     * 按分类获取已上架商品
     */
    @Cacheable(sync = true)
    public List<String> getCatIds(String brandId, String keywords, Boolean isHot, Boolean isNew) {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        if (!Objects.isNull(brandId)) {
            wrapper.eq(LitemallGoods.BRAND_ID , brandId);
        }
        if (!Objects.isNull(isNew)) {
            wrapper.eq(LitemallGoods.IS_NEW , isNew);
        }
        if (!Objects.isNull(isHot)) {
            wrapper.eq(LitemallGoods.IS_HOT , isHot);
        }
        if (!Objects.isNull(keywords)) {
            wrapper.like(LitemallGoods.KEYWORDS , keywords);
            wrapper.like(LitemallGoods.NAME , keywords);
        }
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        List<LitemallGoods> goodsList = queryAll(wrapper);
        List<String> cats = new ArrayList<>();
        for (LitemallGoods goods : goodsList) {
            cats.add(goods.getCategoryId());
        }
        return cats;
    }



    /**
     * 更具商品ID列表查询商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByIds(String[] ids) {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        wrapper.in(LitemallGoods.ID , Arrays.asList(ids));
        wrapper.eq(LitemallGoods.STATUS , GoodsStatus.GOODS_ON_SALE.getStatus());
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


}
