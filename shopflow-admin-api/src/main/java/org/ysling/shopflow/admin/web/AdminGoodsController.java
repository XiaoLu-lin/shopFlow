package org.ysling.shopflow.admin.web;
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
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.service.*;
import org.ysling.shopflow.admin.model.goods.body.GoodsListBody;
import org.ysling.shopflow.admin.model.goods.result.CatAndBrandResult;
import org.ysling.shopflow.admin.model.goods.result.GoodsDetailResult;
import org.ysling.shopflow.core.service.GoodsCoreService;
import org.ysling.shopflow.core.tasks.impl.GrouponRuleExpiredTask;
import org.ysling.shopflow.core.tasks.service.TaskService;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.db.entity.BaseOption;
import org.ysling.shopflow.db.entity.GoodsAllinone;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.db.enums.GrouponRuleStatus;

import javax.validation.Valid;
import org.ysling.shopflow.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;


/**
 * 商品管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/goods")
@Validated
public class AdminGoodsController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private AdminGoodsService goodsService;
    @Autowired
    private AdminGoodsSpecificationService specificationService;
    @Autowired
    private AdminGoodsAttributeService attributeService;
    @Autowired
    private AdminGrouponRulesService rulesService;
    @Autowired
    private AdminGoodsProductService productService;
    @Autowired
    private AdminCategoryService categoryService;
    @Autowired
    private AdminBrandService brandService;
    @Autowired
    private AdminRewardTaskService rewardTaskService;
    @Autowired
    private GoodsCoreService goodsCoreService;

    /**
     * 查询商品
     */
    @SaCheckPermission("admin:goods:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(GoodsListBody body) {
        return ResponseUtil.okList(goodsService.querySelective(body));
    }

    /**
     * 获取店铺与分类选择列表
     */
    @GetMapping("/catAndBrand")
    public Object catAndBrand() {
        // 管理员设置“所属分类”
        List<ShopflowCategory> l1CatList = categoryService.queryL1();
        List<BaseOption> categoryList = new ArrayList<>(l1CatList.size());
        //一级分类
        for (ShopflowCategory l1 : l1CatList) {
            BaseOption l1CatVo = new BaseOption();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());
            //二级分类
            List<ShopflowCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<BaseOption> children = new ArrayList<>(l2CatList.size());
            for (ShopflowCategory l2 : l2CatList) {
                BaseOption l2CatVo = new BaseOption();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);
            categoryList.add(l1CatVo);
        }
        // 管理员设置“所属品牌商”
        List<ShopflowBrand> list = brandService.all();
        List<BaseOption> brandList = new ArrayList<>(l1CatList.size());
        for (ShopflowBrand brand : list) {
            BaseOption option = new BaseOption();
            option.setValue(brand.getId());
            option.setLabel(brand.getName());
            brandList.add(option);
        }
        //店铺与分类选择列表
        CatAndBrandResult result = new CatAndBrandResult();
        result.setBrandList(brandList);
        result.setCategoryList(categoryList);
        return ResponseUtil.ok(result);
    }

    /**
     * 编辑商品
     */
    @SaCheckPermission("admin:goods:update")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "编辑商品")
    @PostMapping("/update")
    public Object update(@Valid @RequestBody GoodsAllinone goodsAllinone) {
        return goodsCoreService.goodsUpdate(goodsAllinone);
    }

    /**
     * 删除商品
     */
    @SaCheckPermission("admin:goods:delete")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "删除商品")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        return goodsCoreService.goodsDelete(id);
    }

    /**
     * 商品上下架
     */
    @SaCheckPermission("admin:goods:on-sale")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "商品上下架")
    @PostMapping("/on-sale")
    public Object updateStatus(@Valid @RequestBody ShopflowGoods goods) {
        String gid = goods.getId();
        ShopflowGoods shopflowGoods = goodsService.findById(gid);
        if (gid == null || shopflowGoods == null) {
            return ResponseUtil.badArgument();
        }
        if (goodsService.updateVersionSelective(goods) == 0){
            return ResponseUtil.updatedDataFailed();
        }

        ShopflowGrouponRules grouponRules = rulesService.findByGid(gid);
        if (grouponRules != null){
            if (!GoodsStatus.getIsOnSale(goods)){
                //删除团购超时任务
                taskService.removeTask(new GrouponRuleExpiredTask(grouponRules));
                //设置团购立马过期
                GrouponRuleExpiredTask expiredTask = new GrouponRuleExpiredTask(grouponRules);
                expiredTask.run();
            }else {
                grouponRules.setStatus(GrouponRuleStatus.RULE_STATUS_ON.getStatus());
            }
            if (rulesService.updateVersionSelective(grouponRules) == 0) {
                throw new RuntimeException("更新数据失败");
            }
        }
        //如果是下架删除团购任务取消赏金
        if (!GoodsStatus.getIsOnSale(goods)){
            //删除赏金
            ShopflowRewardTask rewardTask = rewardTaskService.findByGid(goods.getId());
            if (rewardTask != null) {
                rewardTaskService.deleteByGid(goods.getId());
            }
        }
        return ResponseUtil.ok();
    }

    /**
     * 添加商品
     */
    @SaCheckPermission("admin:goods:create")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "添加商品")
    @PostMapping("/create")
    public Object create(@Valid @RequestBody GoodsAllinone goodsAllinone) {
        return goodsCoreService.goodsCreate(goodsAllinone);
    }

    /**
     * 商品详情
     */
    @SaCheckPermission("admin:goods:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "商品管理"}, button = "商品详情")
    @GetMapping("/detail")
    public Object detail(@JsonBody String id) {
        ShopflowGoods goods = goodsService.findById(id);
        if (goods == null){
            return ResponseUtil.fail(600,"商品不存在");
        }
        List<ShopflowGoodsProduct> products = productService.queryByGid(id);
        List<ShopflowGrouponRules> grouponRules = rulesService.queryByGoodsId(id);
        List<ShopflowGoodsSpecification> specifications = specificationService.queryByGid(id);
        List<ShopflowGoodsAttribute> attributes = attributeService.queryByGid(id);

        //商品分类信息
        String categoryId = goods.getCategoryId();
        ShopflowCategory category = categoryService.findById(categoryId);
        String[] categoryIds = new String[]{};
        if (category != null) {
            categoryIds = new String[]{category.getPid(), categoryId};
        }

        //商品详情
        GoodsDetailResult result = new GoodsDetailResult();
        result.setGoods(goods);
        result.setAttributes(attributes);
        result.setCategoryIds(categoryIds);
        result.setProducts(products);
        result.setSpecifications(specifications);
        if (goods.getIsGroupon() && grouponRules.size()>0){
            result.setGrouponRules(grouponRules.get(0));
        }
        return ResponseUtil.ok(result);
    }

}
