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
import org.ysling.litemall.db.domain.LitemallGoodsProduct;
import org.ysling.litemall.db.service.impl.GoodsProductServiceImpl;
import java.util.List;

/**
 * 商品货品服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods_product")
public class WxGoodsProductService extends GoodsProductServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallGoodsProduct> queryByGid(String goodsId) {
        QueryWrapper<LitemallGoodsProduct> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsProduct.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }

}