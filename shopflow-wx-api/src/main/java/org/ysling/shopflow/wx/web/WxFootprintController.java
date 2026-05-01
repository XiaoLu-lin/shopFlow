package org.ysling.shopflow.wx.web;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.db.entity.PageBody;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.wx.web.impl.WxWebFootprintService;

/**
 * 用户访问足迹服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/footprint")
@Validated
public class WxFootprintController {

    @Autowired
    private WxWebFootprintService footprintService;


    /**
     * 用户足迹列表
     */
    @GetMapping("list")
    public Object list(@LoginUser String userId, PageBody body) {
        return footprintService.list(userId , body);
    }

    /**
     * 删除用户足迹
     * @param userId 用户ID
     * @param id   足迹ID
     * @return 删除操作结果
     */
    @PostMapping("delete")
    public Object delete(@LoginUser String userId, @JsonBody String id) {
        return footprintService.delete(userId , id);
    }



}