package org.ysling.shopflow.admin.service;
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
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.goods.body.GoodsListBody;
import org.ysling.shopflow.db.domain.ShopflowBrand;
import org.ysling.shopflow.db.domain.ShopflowGoods;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.ysling.shopflow.db.service.impl.GoodsServiceImpl;
import java.util.Arrays;
import java.util.List;


/**
 * 商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_goods")
public class AdminGoodsService extends GoodsServiceImpl {


    /**
     * 获取店铺下的所有商品
     */
    @Cacheable(sync = true)
    public List<ShopflowGoods> queryByBrand(String brandId) {
        QueryWrapper<ShopflowGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoods.BRAND_ID , brandId);
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 管理后台查询商品信息
     */
    
    @Cacheable(sync = true)
    public List<ShopflowGoods> querySelective(GoodsListBody body) {
        QueryWrapper<ShopflowGoods> wrapper = startPage(body);
        if (body.getStatus() != null){
            wrapper.eq(ShopflowGoods.STATUS , body.getStatus());
        }
        if (body.getGoodsId() != null) {
            wrapper.eq(ShopflowGoods.ID , body.getGoodsId());
        }
        if (body.getBrandId() != null) {
            wrapper.eq(ShopflowGoods.BRAND_ID , body.getBrandId());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(ShopflowGoods.NAME , body.getName());
        }
        if (StringUtils.hasText(body.getGoodsSn())) {
            wrapper.like(ShopflowGoods.GOODS_SN , body.getGoodsSn());
        }
        wrapper.orderByDesc(ShopflowGoods.WEIGHT);
        return queryAll(wrapper);
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
