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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.utils.RegexUtil;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallBrand;
import org.ysling.litemall.db.enums.BrandStatus;
import org.ysling.litemall.db.service.impl.BrandServiceImpl;
import org.ysling.litemall.wx.model.brand.body.BrandListBody;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 店铺服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_brand")
public class WxBrandService extends BrandServiceImpl {


    public Object validate(LitemallBrand brand) {
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
        LitemallBrand byBrandName = findByBrandName(brand.getName());
        if (byBrandName != null && !Objects.equals(brand.getUserId(),byBrandName.getUserId())){
            return ResponseUtil.fail("店铺名称已存在");
        }
        return null;
    }
    
    @Cacheable(sync = true)
    public List<LitemallBrand> queryList(BrandListBody body) {
        QueryWrapper<LitemallBrand> wrapper = startPage(body);
        wrapper.eq(LitemallBrand.STATUS , BrandStatus.STATUS_NORMAL.getStatus());
        if (StringUtils.hasText(body.getBrandName())){
            wrapper.like(LitemallBrand.NAME , body.getBrandName());
        }
        wrapper.orderByDesc(LitemallBrand.WEIGHT);
        return queryAll(wrapper);
    }

    @Cacheable(sync = true)
    public List<LitemallBrand> queryByUserId(String userId) {
        QueryWrapper<LitemallBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallBrand.USER_ID , userId);
        return queryAll(wrapper);
    }

    
    @Cacheable(sync = true)
    public LitemallBrand findByUserId(String userId) {
        QueryWrapper<LitemallBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallBrand.USER_ID , userId);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallBrand findByUserId(String userId , String brandId) {
        QueryWrapper<LitemallBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallBrand.USER_ID , userId);
        wrapper.eq(LitemallBrand.ID , brandId);
        return getOne(wrapper);
    }

    @Cacheable(sync = true)
    public LitemallBrand findByBrandName(String name) {
        QueryWrapper<LitemallBrand> wrapper = new QueryWrapper<>();
        wrapper.eq(LitemallBrand.NAME , name);
        return getOne(wrapper , false);
    }


}
