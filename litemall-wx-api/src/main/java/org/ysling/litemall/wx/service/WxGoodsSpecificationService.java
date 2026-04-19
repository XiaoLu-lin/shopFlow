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
import org.ysling.litemall.db.domain.LitemallGoodsSpecification;
import org.ysling.litemall.db.entity.GoodsSpecificationVo;
import org.ysling.litemall.db.service.impl.GoodsSpecificationServiceImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商品规格服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_goods_specification")
public class WxGoodsSpecificationService extends GoodsSpecificationServiceImpl {


    @Cacheable(sync = true)
    public List<LitemallGoodsSpecification> queryByGid(String goodsId) {
        QueryWrapper<LitemallGoodsSpecification> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallGoodsSpecification.GOODS_ID , goodsId);
        return queryAll(wrapper);
    }


    @Cacheable(sync = true)
    public List<GoodsSpecificationVo> getSpecificationVoList(String goodsId) {
        List<LitemallGoodsSpecification> goodsSpecificationList = queryByGid(goodsId);
        Map<String, GoodsSpecificationVo> map = new HashMap<>();
        List<GoodsSpecificationVo> specificationVoList = new ArrayList<>();
        for (LitemallGoodsSpecification goodsSpecification : goodsSpecificationList) {
            String specification = goodsSpecification.getSpecification();
            GoodsSpecificationVo goodsSpecificationVo = map.get(specification);
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new GoodsSpecificationVo();
                goodsSpecificationVo.setName(specification);
                List<LitemallGoodsSpecification> valueList = new ArrayList<>();
                valueList.add(goodsSpecification);
                goodsSpecificationVo.setValueList(valueList);
                map.put(specification, goodsSpecificationVo);
                specificationVoList.add(goodsSpecificationVo);
            } else {
                List<LitemallGoodsSpecification> valueList = goodsSpecificationVo.getValueList();
                valueList.add(goodsSpecification);
            }
        }
        return specificationVoList;
    }


}
