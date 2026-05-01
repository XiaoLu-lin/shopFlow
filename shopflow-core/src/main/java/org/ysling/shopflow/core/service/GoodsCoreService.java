package org.ysling.shopflow.core.service;
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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.db.service.*;
import org.ysling.shopflow.db.entity.GoodsAllinone;
import org.ysling.shopflow.core.tasks.impl.GrouponRuleExpiredTask;
import org.ysling.shopflow.core.tasks.service.TaskService;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.db.enums.BrandStatus;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.ysling.shopflow.db.enums.GrouponRuleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Ysling
 */
@Slf4j
@Service
public class GoodsCoreService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IGoodsSpecificationService specificationService;
    @Autowired
    private IGoodsAttributeService attributeService;
    @Autowired
    private IGoodsProductService productService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IGrouponRulesService grouponRulesService;
    @Autowired
    private IRewardTaskService rewardTaskService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private QrcodeCoreService qCodeService;
    @Autowired
    private CommonService commonService;

    /**
     * 商品信息校验
     */
    private Object validate(GoodsAllinone goodsAllinone) {
        ShopflowGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        String unit = goods.getUnit();
        String brief = goods.getBrief();
        String picUrl = goods.getPicUrl();
        String goodsSn = goods.getGoodsSn();
        if (!ObjectUtils.allNotNull(name, goodsSn,unit,brief,picUrl)) {
            return ResponseUtil.fail("商品信息错误");
        }

        BigDecimal counterPrice = goods.getCounterPrice();
        if (counterPrice.intValue() > 9999999){
            return ResponseUtil.fail("市场售价不能太高");
        }

        String[] gallery = goods.getGallery();
        if (gallery == null || gallery.length <= 0){
            return ResponseUtil.fail("商品轮播图错误");
        }

        // 品牌商必须设置，如果设置则需要验证品牌商存在
        String brandId = goods.getBrandId();
        if (brandId != null && !brandId.equals("0")) {
            ShopflowBrand brand = brandService.findById(brandId);
            if (brand == null) {
                return ResponseUtil.fail("店铺信息错误");
            }

            if (brand.getStatus().equals(BrandStatus.STATUS_DISABLED.getStatus())){
                return ResponseUtil.fail("店铺被禁用");
            }

            if (brand.getStatus().equals(BrandStatus.STATUS_OUT.getStatus())){
                return ResponseUtil.fail("店铺被注销");
            }
        }else {
            return ResponseUtil.fail("店铺信息错误");
        }

        // 分类必须设置，如果设置则需要验证分类存在
        String categoryId = goods.getCategoryId();
        if (categoryId != null && !categoryId.equals("0")) {
            if (categoryService.findById(categoryId) == null) {
                return ResponseUtil.fail("分类信息错误");
            }
        }else {
            return ResponseUtil.fail("分类信息错误");
        }

        //如果有团购验证团购
        ShopflowGrouponRules grouponRules = goodsAllinone.getGrouponRules();
        if (goods.getIsGroupon() != null && goods.getIsGroupon() && grouponRules != null){
            BigDecimal discount = grouponRules.getDiscount();
            LocalDateTime expireTime = grouponRules.getExpireTime();
            Integer discountMember = grouponRules.getDiscountMember();
            if (!ObjectUtils.allNotNull(expireTime, discount, discountMember)){
                return ResponseUtil.fail("团购信息错误");
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = grouponRules.getExpireTime();
            if(expire.isBefore(now)){
                return ResponseUtil.fail(889,"团购日期设置错误");
            }
        }

        //商品规格设置
        ShopflowGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (ShopflowGoodsSpecification specification : specifications) {
            String value = specification.getValue();
            String spec = specification.getSpecification();
            String specificationPicUrl = specification.getPicUrl();
            if (!ObjectUtils.allNotNull(spec, value ,specificationPicUrl)) {
                return ResponseUtil.fail("商品规格信息不完整");
            }
        }

        //商品参数设置
        ShopflowGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (ShopflowGoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            String value = attribute.getValue();
            if (!ObjectUtils.allNotNull(attr, value)) {
                return ResponseUtil.fail("商品参数信息不完整");
            }
        }

        //商品货品设置
        ShopflowGoodsProduct[] products = goodsAllinone.getProducts();
        if (products.length <= 0) {
            return ResponseUtil.fail("货品信息至少一条");
        }
        for (ShopflowGoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number <= 0) {
                return ResponseUtil.fail("货品库存不正确");
            }
            BigDecimal price = product.getPrice();
            if (price == null || price.intValue() < 0) {
                return ResponseUtil.fail("货品销售价格不正确,不能小于0元");
            }
            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length <= 0) {
                return ResponseUtil.fail("货品规格至少有一个");
            }
        }

        return null;
    }





    /**
     * 商品添加
     */
    public Object goodsCreate(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        ShopflowGoods goods = goodsAllinone.getGoods();
        ShopflowGrouponRules grouponRules = goodsAllinone.getGrouponRules();
        ShopflowGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        ShopflowGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        ShopflowGoodsProduct[] products = goodsAllinone.getProducts();

        if (commonService.checkExistByName(goods.getName())) {
            throw new RuntimeException("商品名已经存在");
        }

        // 商品表里面有一个字段retailPrice记录当前商品的最低价
        BigDecimal retailPrice = BigDecimal.valueOf(Integer.MAX_VALUE);
        for (ShopflowGoodsProduct product : products) {
            BigDecimal productPrice = product.getPrice();
            if(retailPrice.compareTo(productPrice) > 0){
                retailPrice = productPrice;
            }
        }

        // 添加商品基本信息
        goods.setRetailPrice(retailPrice);
        goodsService.add(goods);

        //将生成的分享图片地址写入数据库
        this.createGoodShareImage(goods);

        //判断是否团购
        if (goods.getIsGroupon() != null && goods.getIsGroupon()){
            //添加团购规则信息
            grouponRules.setGoodsId(goods.getId());
            grouponRules.setGoodsName(goods.getName());
            grouponRules.setPicUrl(goods.getPicUrl());
            grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_ON.getStatus());
            grouponRulesService.add(grouponRules);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = grouponRules.getExpireTime();
            long delay = ChronoUnit.MILLIS.between(now, expire);
            // 团购过期任务
            taskService.addTask(new GrouponRuleExpiredTask(grouponRules, delay));
        }

        // 商品规格表shopflow_goods_specification
        for (ShopflowGoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            specificationService.add(specification);
        }

        // 商品参数表shopflow_goods_attribute
        for (ShopflowGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.add(attribute);
        }

        // 商品货品表shopflow_product
        for (ShopflowGoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.add(product);
        }
        return ResponseUtil.ok();
    }

    @Async
    void createGoodShareImage(ShopflowGoods goods){
        //将生成的分享图片地址写入数据库
        goods.setShareUrl(qCodeService.createGoodShareImage(goods));
        goodsService.updateSelective(goods);
    }


    /**
     * 商品更新
     */
    public Object goodsUpdate(GoodsAllinone goodsAllinone) {
        Object error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        ShopflowGoods goods = goodsAllinone.getGoods();
        ShopflowGrouponRules grouponRules = goodsAllinone.getGrouponRules();
        ShopflowGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        ShopflowGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        ShopflowGoodsProduct[] products = goodsAllinone.getProducts();

        // 商品表里面有一个字段retailPrice记录当前商品的最低价
        BigDecimal retailPrice = BigDecimal.valueOf(Integer.MAX_VALUE);
        for (ShopflowGoodsProduct product : products) {
            BigDecimal productPrice = product.getPrice();
            if(retailPrice.compareTo(productPrice) > 0){
                retailPrice = productPrice;
            }
        }

        // 商品基本信息
        goods.setRetailPrice(retailPrice);
        if (goodsService.updateVersionSelective(goods) == 0){
            throw new RuntimeException("网络繁忙，请刷新重试");
        }

        //将生成的分享图片地址写入数据库
        if (!StringUtils.hasText(goods.getShareUrl())){
            this.createGoodShareImage(goods);
        }

        //商品下架
        if (!GoodsStatus.getIsOnSale(goods)){
            //删除赏金
            ShopflowRewardTask rewardTask = commonService.findByRewardTaskGid(goods.getId());
            if (rewardTask != null) {
                commonService.deleteByRewardTaskGid(goods.getId());
            }
        } else {
            ShopflowRewardTask rewardTask = commonService.findByRewardTaskGid(goods.getId());
            if (rewardTask != null) {
                rewardTask.setPicUrl(goods.getPicUrl());
                rewardTask.setGoodsName(goods.getName());
                rewardTask.setGoodsPrice(goods.getRetailPrice());
                if (rewardTaskService.updateVersionSelective(rewardTask) == 0){
                    throw new RuntimeException("赏金更新失效");
                }
            }
        }

        if (goods.getIsGroupon() != null && goods.getIsGroupon() && grouponRules != null){
            //设置商品更新信息
            grouponRules.setGoodsId(goods.getId());
            grouponRules.setPicUrl(goods.getPicUrl());
            grouponRules.setGoodsName(goods.getName());
            if (!GoodsStatus.getIsOnSale(goods)){
                grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_OFF.getStatus());
            }else {
                grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_ON.getStatus());
            }
            if (grouponRules.getId() != null){
                if (grouponRulesService.updateVersionSelective(grouponRules) == 0) {
                    throw new RuntimeException("更新数据失败");
                }
            }else {
                //添加团购信息
                if (grouponRulesService.add(grouponRules) == 0) {
                    throw new RuntimeException("更新数据失败");
                }
            }
            if (GoodsStatus.getIsOnSale(goods)){
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expire = grouponRules.getExpireTime();
                long delay = ChronoUnit.MILLIS.between(now, expire);
                // 团购过期任务
                taskService.addTask(new GrouponRuleExpiredTask(grouponRules, delay));
            } else {
                //删除团购超时任务
                taskService.removeTask(new GrouponRuleExpiredTask(grouponRules));
                //设置团购立马过期
                GrouponRuleExpiredTask expiredTask = new GrouponRuleExpiredTask(grouponRules);
                expiredTask.run();
            }
        } else {
            if (grouponRules != null && commonService.countByGoodsId(goods.getId()) > 0){
                //删除团购超时任务
                taskService.removeTask(new GrouponRuleExpiredTask(grouponRules));
                //设置团购立马过期
                GrouponRuleExpiredTask expiredTask = new GrouponRuleExpiredTask(grouponRules);
                expiredTask.run();
            }
        }

        // 商品规格
        for (ShopflowGoodsSpecification specification : specifications) {
            // 目前只支持更新规格表的图片字段
            specification.setSpecification(null);
            specification.setValue(null);
            if (specificationService.updateVersionSelective(specification) == 0){
                throw new RuntimeException("网络繁忙，请刷新重试");
            }
        }

        // 商品货品
        for (ShopflowGoodsProduct product : products) {
            if (productService.updateVersionSelective(product) == 0){
                throw new RuntimeException("网络繁忙，请刷新重试");
            }
        }

        // 商品参数
        for (ShopflowGoodsAttribute attribute : attributes) {
            if (attributeService.updateVersionSelective(attribute) == 0){
                throw new RuntimeException("网络繁忙，请刷新重试");
            }
        }

        // 这里需要注意的是购物车有些字段是拷贝商品的一些字段，因此需要及时更新
        // 目前这些字段是goods_sn, goods_name, price, pic_url
        for (ShopflowGoodsProduct product : products) {
            commonService.updateProduct(product.getId(), goods.getGoodsSn(), goods.getName(), product.getPrice(), product.getUrl());
        }
        return ResponseUtil.ok();
    }





    /**
     * 商品删除
     */
    public Object goodsDelete(String goodsId) {
        ShopflowGoods goods = goodsService.findById(goodsId);
        if (goodsId == null || goods == null) {
            return ResponseUtil.badArgument();
        }

        if (goods.getIsGroupon() != null && goods.getIsGroupon()){
            ShopflowGrouponRules grouponRules = commonService.findByGrouponRulesGid(goodsId);
            //删除团购超时任务
            taskService.removeTask(new GrouponRuleExpiredTask(grouponRules));
            //设置团购立马过期
            GrouponRuleExpiredTask expiredTask = new GrouponRuleExpiredTask(grouponRules);
            expiredTask.run();
        }

        //删除赏金
        ShopflowRewardTask rewardTask = commonService.findByRewardTaskGid(goodsId);
        if (rewardTask != null) {
            commonService.deleteByRewardTaskGid(goodsId);
        }

        //删除商品信息
        goodsService.deleteById(goodsId);
        commonService.deleteBySpecificationGid(goodsId);
        commonService.deleteByAttributeGid(goodsId);
        commonService.deleteByProductGid(goodsId);
        return ResponseUtil.ok();
    }




}