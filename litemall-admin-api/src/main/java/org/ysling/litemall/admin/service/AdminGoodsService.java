package org.ysling.litemall.admin.service;
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
import org.springframework.util.StringUtils;
import org.ysling.litemall.admin.model.goods.body.GoodsListBody;
import org.ysling.litemall.db.domain.LitemallBrand;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.enums.GoodsStatus;
import org.ysling.litemall.db.service.impl.GoodsServiceImpl;
import java.util.Arrays;
import java.util.List;


/**
 * 商品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods")
public class AdminGoodsService extends GoodsServiceImpl {


    /**
     * 获取店铺下的所有商品
     */
    @Cacheable(sync = true)
    public List<LitemallGoods> queryByBrand(String brandId) {
        QueryWrapper<LitemallGoods> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoods.BRAND_ID , brandId);
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
    }


    /**
     * 管理后台查询商品信息
     */
    
    @Cacheable(sync = true)
    public List<LitemallGoods> querySelective(GoodsListBody body) {
        QueryWrapper<LitemallGoods> wrapper = startPage(body);
        if (body.getStatus() != null){
            wrapper.eq(LitemallGoods.STATUS , body.getStatus());
        }
        if (body.getGoodsId() != null) {
            wrapper.eq(LitemallGoods.ID , body.getGoodsId());
        }
        if (body.getBrandId() != null) {
            wrapper.eq(LitemallGoods.BRAND_ID , body.getBrandId());
        }
        if (StringUtils.hasText(body.getName())) {
            wrapper.like(LitemallGoods.NAME , body.getName());
        }
        if (StringUtils.hasText(body.getGoodsSn())) {
            wrapper.like(LitemallGoods.GOODS_SN , body.getGoodsSn());
        }
        wrapper.orderByDesc(LitemallGoods.WEIGHT);
        return queryAll(wrapper);
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
