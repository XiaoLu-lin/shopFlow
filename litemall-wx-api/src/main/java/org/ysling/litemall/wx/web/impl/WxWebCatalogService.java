package org.ysling.litemall.wx.web.impl;
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallCategory;
import org.ysling.litemall.wx.model.catalog.body.CatalogIndexResult;
import org.ysling.litemall.wx.service.WxCategoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类目服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebCatalogService {

    @Autowired
    private WxCategoryService categoryService;

    /**
     * 分类详情
     * @param id   分类类目ID
     * @return 分类详情
     */
    public Object index(String id) {
        // 所有一级分类目录
        List<LitemallCategory> categoryList = categoryService.queryL1();
        // 当前一级分类目录
        LitemallCategory currentCategory = null;
        if (id != null) {
            currentCategory = categoryService.findById(id);
        } else {
             if (categoryList.size() > 0) {
                currentCategory = categoryList.get(0);
            }
        }
        // 当前一级分类目录对应的二级分类目录
        List<LitemallCategory> currentSubCategory = null;
        if (currentCategory != null) {
            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
        }
        CatalogIndexResult result = new CatalogIndexResult();
        result.setCategoryList(categoryList);
        result.setCurrentCategory(currentCategory);
        result.setCurrentSubCategory(currentSubCategory);
        return ResponseUtil.ok(result);
    }

    /**
     * 所有分类数据
     * @return 所有分类数据
     */
    public Object queryAll() {
        // 所有一级分类目录
        List<LitemallCategory> l1CatList = categoryService.queryL1();
        //所有子分类列表
        Map<String, List<LitemallCategory>> allList = new HashMap<>();
        List<LitemallCategory> sub;
        for (LitemallCategory category : l1CatList) {
            sub = categoryService.queryByPid(category.getId());
            allList.put(category.getId(), sub);
        }
        // 当前一级分类目录
        LitemallCategory currentCategory = l1CatList.get(0);
        // 当前一级分类目录对应的二级分类目录
        List<LitemallCategory> currentSubCategory = null;
        if (null != currentCategory) {
            currentSubCategory = categoryService.queryByPid(currentCategory.getId());
        }
        CatalogIndexResult result = new CatalogIndexResult();
        result.setAllList(allList);
        result.setCategoryList(l1CatList);
        result.setCurrentCategory(currentCategory);
        result.setCurrentSubCategory(currentSubCategory);
        return ResponseUtil.ok(result);
    }

    /**
     * 当前分类栏目
     * @param id 分类类目ID
     */
    public Object current(String id) {
        // 当前分类
        LitemallCategory currentCategory = categoryService.findById(id);
        if(currentCategory == null){
            return ResponseUtil.badArgumentValue();
        }
        CatalogIndexResult result = new CatalogIndexResult();
        result.setCurrentCategory(currentCategory);
        result.setCurrentSubCategory(categoryService.queryByPid(currentCategory.getId()));
        return ResponseUtil.ok(result);
    }

    /**
     * 所有一级分类目录
     */
    public Object first() {
        return ResponseUtil.ok(categoryService.queryL1());
    }

    /**
     * 所有二级分类目录
     * @param id 一级分类ID
     */
    public Object second(String id) {
        return ResponseUtil.ok(categoryService.queryByPid(id));
    }


}
