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
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.wx.service.WxFootprintService;

/**
 * 用户访问足迹服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebFootprintService {


    @Autowired
    private WxFootprintService footprintService;

    /**
     * 用户足迹列表
     */
    public Object list(String userId, PageBody body) {
        return ResponseUtil.okList(footprintService.queryByAddTime(userId, body));
    }

    /**
     * 删除用户足迹
     * @param userId 用户ID
     * @param id   足迹ID
     * @return 删除操作结果
     */
    public Object delete(String userId, String id) {
        if (footprintService.findById(userId, id) == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (footprintService.deleteById(id) == 0){
            return ResponseUtil.fail("删除失败请重试");
        }
        return ResponseUtil.ok();
    }


}