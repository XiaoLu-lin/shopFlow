package org.ysling.litemall.wx.web;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.litemall.wx.web.impl.WxWebCatalogService;
import org.ysling.litemall.core.annotation.JsonBody;
/**
 * 类目服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/catalog")
@Validated
public class WxCatalogController {

    @Autowired
    private WxWebCatalogService catalogService;

    /**
     * 分类详情
     * @param id   分类类目ID。
     * @return 分类详情
     */
    @GetMapping("index")
    public Object index(String id) {
        return catalogService.index(id);
    }

    /**
     * 所有分类数据
     * @return 所有分类数据
     */
    @GetMapping("all")
    public Object queryAll() {
        return catalogService.queryAll();
    }

    /**
     * 当前分类栏目
     * @param id 分类类目ID
     */
    @GetMapping("current")
    public Object current(@JsonBody String id) {
        return catalogService.current(id);
    }

    /**
     * 所有一级分类目录
     */
    @GetMapping("first")
    public Object first() {
        return catalogService.first();
    }

    /**
     * 所有二级分类目录
     * @param id 一级分类ID
     */
    @GetMapping("second")
    public Object second(@JsonBody String id) {
        return catalogService.second(id);
    }


}
