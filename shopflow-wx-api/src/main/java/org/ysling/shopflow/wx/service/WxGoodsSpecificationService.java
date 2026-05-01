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
import org.ysling.shopflow.db.domain.ShopflowGoodsSpecification;
import org.ysling.shopflow.db.entity.GoodsSpecificationVo;
import org.ysling.shopflow.db.service.impl.GoodsSpecificationServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商品规格服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_goods_specification")
public class WxGoodsSpecificationService extends GoodsSpecificationServiceImpl {


    @Cacheable(sync = true)
    public List<ShopflowGoodsSpecification> queryByGid(String goodsId) {
        QueryWrapper<ShopflowGoodsSpecification> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowGoodsSpecification.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public List<GoodsSpecificationVo> getSpecificationVoList(String goodsId) {
        List<ShopflowGoodsSpecification> goodsSpecificationList = queryByGid(goodsId);
        Map<String, GoodsSpecificationVo> map = new HashMap<>();
        List<GoodsSpecificationVo> specificationVoList = new ArrayList<>();
        for (ShopflowGoodsSpecification goodsSpecification : goodsSpecificationList) {
            String specification = goodsSpecification.getSpecification();
            GoodsSpecificationVo goodsSpecificationVo = map.get(specification);
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new GoodsSpecificationVo();
                goodsSpecificationVo.setName(specification);
                List<ShopflowGoodsSpecification> valueList = new ArrayList<>();
                valueList.add(goodsSpecification);
                goodsSpecificationVo.setValueList(valueList);
                map.put(specification, goodsSpecificationVo);
                specificationVoList.add(goodsSpecificationVo);
            } else {
                List<ShopflowGoodsSpecification> valueList = goodsSpecificationVo.getValueList();
                valueList.add(goodsSpecification);
            }
        }
        return specificationVoList;
    }


}
