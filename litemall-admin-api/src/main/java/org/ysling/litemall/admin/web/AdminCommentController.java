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
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import org.ysling.litemall.admin.model.comment.body.CommentListBody;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.admin.service.AdminGoodsCommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.core.annotation.JsonBody;


/**
 * 商品评论
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
@Validated
public class AdminCommentController {

    @Autowired
    private AdminGoodsCommentService goodsCommentService;


    /**
     * 查询
     */
    @SaCheckPermission("admin:comment:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "评论管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(CommentListBody body) {
        return ResponseUtil.okList(goodsCommentService.querySelective(body));
    }


    /**
     * 删除
     */
    @SaCheckPermission("admin:comment:delete")
    @RequiresPermissionsDesc(menu = {"商场管理", "评论管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@JsonBody String id) {
        if (goodsCommentService.deleteById(id) == 0){
            return ResponseUtil.deletedDataFailed();
        }
        return ResponseUtil.ok();
    }


}
