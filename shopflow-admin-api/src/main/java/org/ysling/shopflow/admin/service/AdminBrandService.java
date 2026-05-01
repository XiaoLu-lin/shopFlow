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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.admin.model.brand.body.BrandListBody;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowBrand;
import org.ysling.shopflow.db.domain.ShopflowCategory;
import org.ysling.shopflow.db.enums.BrandStatus;
import org.ysling.shopflow.db.service.impl.BrandServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 店铺服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "shopflow_brand")
public class AdminBrandService extends BrandServiceImpl {


    public Object validate(ShopflowBrand brand) {
        if (brand == null) {
            return ResponseUtil.badArgument();
        }
        String mail = brand.getMail();
        BigDecimal price = brand.getFloorPrice();
        if (StringUtils.hasText(mail)){
            if (RegexUtil.isQQMail(mail)) {
                return ResponseUtil.fail("QQ邮箱不正确");
            }
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseUtil.fail("店铺最低金额不能小于零");
        }
        ShopflowBrand byBrandName = findByBrandName(brand.getName());
        if (byBrandName != null && !Objects.equals(brand.getUserId(), byBrandName.getUserId())){
            return ResponseUtil.fail("店铺名称已存在");
        }
        return null;
    }

    
    @Cacheable(sync = true)
    public List<ShopflowBrand> all() {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.STATUS , BrandStatus.STATUS_NORMAL.getStatus());
        wrapper.orderByDesc(ShopflowBrand.WEIGHT);
        return list(wrapper);
    }


    @Cacheable(sync = true)
    public List<ShopflowBrand> querySelective(BrandListBody body) {
        QueryWrapper<ShopflowBrand> wrapper = startPage(body);
        if (!Objects.isNull(body.getId())) {
            wrapper.eq(ShopflowBrand.ID , body.getId());
        }
        if (!Objects.isNull(body.getName())) {
            wrapper.like(ShopflowBrand.NAME , body.getName());
        }
        wrapper.orderByDesc(ShopflowBrand.WEIGHT);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public ShopflowBrand findByBrandName(String name) {
        QueryWrapper<ShopflowBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(ShopflowBrand.NAME , name);
        return getOne(wrapper , false);
    }



}
