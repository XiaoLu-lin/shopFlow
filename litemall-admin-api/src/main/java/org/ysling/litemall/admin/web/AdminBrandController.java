package org.ysling.litemall.admin.web;
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
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.model.brand.body.BrandListBody;
import org.ysling.litemall.admin.model.brand.body.BrandSaveBody;
import org.ysling.litemall.core.service.GoodsCoreService;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallBrand;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.ysling.litemall.db.enums.BrandStatus;
import org.ysling.litemall.admin.service.AdminBrandService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.admin.service.AdminGoodsService;
import org.ysling.litemall.db.enums.GoodsStatus;

import javax.validation.Valid;
import org.ysling.litemall.core.annotation.JsonBody;
import java.util.List;

/**
 * 品牌管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/brand")
@Validated
public class AdminBrandController {

    @Autowired
    private AdminBrandService brandService;
    @Autowired
    private AdminGoodsService goodsService;
    @Autowired
    private GoodsCoreService goodsCoreService;

    /**
     * 查询
     */
    @SaCheckPermission("admin:brand:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(BrandListBody body) {
        return ResponseUtil.okList(brandService.querySelective(body));
    }


    /**
     * 详情
     */
    @SaCheckPermission("admin:brand:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@JsonBody String id) {
        return ResponseUtil.ok(brandService.findById(id));
    }

    /**
     * 添加
     */
    @SaCheckPermission("admin:brand:create")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody BrandSaveBody body) {
        LitemallBrand brand = new LitemallBrand();
        BeanUtil.copyProperties(body , brand);
        Object error = brandService.validate(brand);
        if (error != null) {
            return error;
        }
        if (brandService.add(brand) == 0){
            return ResponseUtil.addDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 编辑
     */
    @SaCheckPermission("admin:brand:update")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody LitemallBrand brand) {
        Object error = brandService.validate(brand);
        if (error != null) {
            return error;
        }
        if (brandService.updateVersionSelective(brand) == 0) {
            throw new RuntimeException("店铺更新失败");
        }
        //禁用店铺删除所有商品
        if (!brand.getStatus().equals(BrandStatus.STATUS_NORMAL.getStatus())){
            List<LitemallGoods> goodsList = goodsService.queryByBrand(brand.getId());
            for (LitemallGoods goods :goodsList) {
                goods.setStatus(GoodsStatus.GOODS_UNSOLD.getStatus());
            }
            if (!goodsService.updateBatchById(goodsList)){
                throw new RuntimeException("店铺更新失败");
            }
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除
     */
    @SaCheckPermission("admin:brand:delete")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (brandService.deleteById(id) == 0){
            return ResponseUtil.fail("删除失败请重试");
        }
        List<LitemallGoods> goodsList = goodsService.queryByBrand(id);
        for (LitemallGoods goods :goodsList) {
            goodsCoreService.goodsDelete(goods.getId());
        }
        return ResponseUtil.ok();
    }

}
