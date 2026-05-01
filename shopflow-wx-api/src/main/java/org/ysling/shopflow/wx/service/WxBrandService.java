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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowBrand;
import org.ysling.shopflow.db.enums.BrandStatus;
import org.ysling.shopflow.db.service.impl.BrandServiceImpl;
import org.ysling.shopflow.wx.model.brand.body.BrandListBody;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 店铺服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_brand")
public class WxBrandService extends BrandServiceImpl {


    public Object validate(ShopflowBrand brand) {
        if (brand == null) {
            return ResponseUtil.badArgument();
        }
        String name = brand.getName();
        String picUrl = brand.getPicUrl();
        String depict = brand.getDepict();
        String mail = brand.getMail();
        BigDecimal price = brand.getFloorPrice();
        if (!ObjectUtils.allNotNull(name, depict, picUrl, mail, price)) {
            return ResponseUtil.fail("请完整填写信息");
        }
        if (RegexUtil.isQQMail(mail)) {
            return ResponseUtil.fail("QQ邮箱不正确");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseUtil.fail("店铺最低金额不能小于零");
        }
        ShopflowBrand byBrandName = findByBrandName(brand.getName());
        if (byBrandName != null && !Objects.equals(brand.getUserId(),byBrandName.getUserId())){
            return ResponseUtil.fail("店铺名称已存在");
        }
        return null;
    }
    
    @Cacheable(sync = true)
    public List<ShopflowBrand> queryList(BrandListBody body) {
        QueryWrapper<ShopflowBrand> wrapper = startPage(body);
        wrapper.eq(ShopflowBrand.STATUS , BrandStatus.STATUS_NORMAL.getStatus());
        if (StringUtils.hasText(body.getBrandName())){
            wrapper.like(ShopflowBrand.NAME , body.getBrandName());
        }
        wrapper.orderByDesc(ShopflowBrand.WEIGHT);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<ShopflowBrand> queryByUserId(String userId) {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.USER_ID , userId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public ShopflowBrand findByUserId(String userId) {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.USER_ID , userId);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public ShopflowBrand findByUserId(String userId , String brandId) {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.USER_ID , userId);
        wrapper.eq(ShopflowBrand.ID , brandId);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public ShopflowBrand findByBrandName(String name) {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.NAME , name);
        return getOne(wrapper , false);
    }


}
