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
import org.ysling.litemall.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.wx.model.goods.body.GoodsCommentListBody;
import org.ysling.litemall.wx.model.goods.body.GoodsListBody;
import org.ysling.litemall.wx.web.impl.WxWebGoodsService;
import org.ysling.litemall.core.annotation.JsonBody;

/**
 * 商品服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/goods")
@Validated
public class WxGoodsController {

	@Autowired
	private WxWebGoodsService goodsService;

	/**
	 * 商品详情
	 * @param userId 用户ID
	 * @param goodId     商品ID
	 * @return 商品详情
	 */
	@GetMapping("detail")
	public Object detail(@LoginUser(require = false) String userId, @JsonBody String goodId) {
		return goodsService.detail(userId , goodId);
	}

	/**
	 * 商品分类类目
	 * @param id 分类类目ID
	 * @return 商品分类类目
	 */
	@GetMapping("category")
	public Object category(@JsonBody String id) {
		return goodsService.category(id);
	}

	/**
	 * 根据条件搜素商品
	 * @return 根据条件搜素的商品详情
	 */
	@GetMapping("list")
	public Object list(@LoginUser(require = false) String userId, GoodsListBody body) {
		return goodsService.list(userId , body);
	}

	/**
	 * 商品详情页面“大家都在看”推荐商品
	 * @param goodId, 商品ID
	 * @return 商品详情页面推荐商品
	 */
	@GetMapping("related")
	public Object related(@JsonBody String goodId) {
		return goodsService.related(goodId);
	}

	/**
	 * 在售的商品总数
	 * @return 在售的商品总数
	 */
	@GetMapping("count")
	public Object count() {
		return goodsService.count();
	}

	/**
	 * 评论数量
	 * @param goodsId 商品ID。
	 * @return 评论数量
	 */
	@GetMapping("/comment/count")
	public Object count(@JsonBody String goodsId) {
		return goodsService.commentCount(goodsId);
	}

	/**
	 * 评论列表
	 */
	@GetMapping("/comment/list")
	public Object list(GoodsCommentListBody body) {
		return goodsService.commentList(body);
	}

}