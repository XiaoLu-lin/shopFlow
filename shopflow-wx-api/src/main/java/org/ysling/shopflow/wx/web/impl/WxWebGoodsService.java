package org.ysling.shopflow.wx.web.impl;
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

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ysling.shopflow.core.handler.ThreadPoolHandler;
import org.ysling.shopflow.core.system.enums.SystemConfig;
import org.ysling.shopflow.core.tenant.handler.TenantContextHolder;
import org.ysling.shopflow.core.utils.Inheritable.Inheritable;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.*;
import org.ysling.shopflow.db.entity.GoodsSpecificationVo;
import org.ysling.shopflow.db.enums.CollectType;
import org.ysling.shopflow.db.enums.GoodsStatus;
import org.ysling.shopflow.wx.model.goods.body.GoodsCommentListBody;
import org.ysling.shopflow.wx.model.goods.body.GoodsListBody;
import org.ysling.shopflow.wx.model.goods.result.*;
import org.ysling.shopflow.wx.service.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 商品服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebGoodsService {

	@Autowired
	private WxUserService userService;
	@Autowired
	private WxGoodsService goodsService;
	@Autowired
	private WxGoodsProductService productService;
	@Autowired
	private WxIssueService goodsIssueService;
	@Autowired
	private WxGoodsAttributeService goodsAttributeService;
	@Autowired
	private WxBrandService brandService;
	@Autowired
	private WxCollectService collectService;
	@Autowired
	private WxFootprintService footprintService;
	@Autowired
	private WxCategoryService categoryService;
	@Autowired
	private WxSearchHistoryService searchHistoryService;
	@Autowired
	private WxGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private WxGrouponRulesService grouponRulesService;
	@Autowired
	private WxGoodsCommentService goodsCommentService;
	@Autowired
	private ThreadPoolHandler threadPoolHandler;

	/**
	 * 商品详情
	 * <p>
	 * 用户可以不登录。
	 * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
	 *
	 * @param userId 用户ID
	 * @param goodId     商品ID
	 * @return 商品详情
	 */
	public Object detail(String userId, String goodId) {
		// 商品信息
		ShopflowGoods info = goodsService.findById(goodId);
		if(info == null || info.getBrandId() == null || !GoodsStatus.getIsOnSale(info)){
			return ResponseUtil.fail(600,"商品已下架");
		}

		// 评论列表
		Inheritable<GoodsCommentResult> commentsCallableTsk = new Inheritable<>(
				() -> goodsCommentService.getComments(goodId, 2)
		);

		// 商品属性
		Inheritable<List<ShopflowGoodsAttribute>> goodsAttributeListTask = new Inheritable<>(
				() -> goodsAttributeService.queryByGid(goodId)
		);

		// 商品规格 返回的是定制的GoodsSpecificationVo
		Inheritable<List<GoodsSpecificationVo>> specificationCallableTask = new Inheritable<>(
				() -> goodsSpecificationService.getSpecificationVoList(goodId)
		);

		// 商品规格对应的数量和价格
		Inheritable<List<ShopflowGoodsProduct>> productListCallableTask = new Inheritable<>(
				() -> productService.queryByGid(goodId)
		);

		// 商品问题，这里是一些通用问题
		Inheritable<List<ShopflowIssue>> issueCallableTask = new Inheritable<>(
				() -> goodsIssueService.getGoodsIssue()
		);

		// 商品品牌商
		Inheritable<ShopflowBrand> brandCallableTask = new Inheritable<>(
				() -> brandService.findById(info.getBrandId())
		);

		// 团购信息
		Inheritable<List<ShopflowGrouponRules>> grouponRulesCallableTask = new Inheritable<>(
				() -> grouponRulesService.queryOnByGoodsId(goodId)
		);

		// 用户是否收藏
		Inheritable<Boolean> collectCallableTask = new Inheritable<>(
				() -> collectService.count(userId, CollectType.TYPE_GOODS, goodId)
		);

		// 记录用户的足迹
		threadPoolHandler.start(TenantContextHolder.getLocalTenantId(),
				()-> footprintService.createFootprint(userId, info)
		);

		GoodsDetailResult result = new GoodsDetailResult();
		try {
			result.setInfo(info);
			result.setShareImage(info.getShareUrl());
			result.setUserHasCollect(collectCallableTask.get());
			result.setIssue(issueCallableTask.get());
			result.setComment(commentsCallableTsk.get());
			result.setSpecificationList(specificationCallableTask.get());
			result.setProductList(productListCallableTask.get());
			result.setAttribute(goodsAttributeListTask.get());
			result.setBrand(brandCallableTask.get());
			result.setGroupon(grouponRulesCallableTask.get());
			result.setShare(SystemConfig.isAutoCreateShareImage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseUtil.ok(result);
	}

	/**
	 * 商品分类类目
	 *
	 * @param id 分类类目ID
	 * @return 商品分类类目
	 */
	public Object category(String id) {
		ShopflowCategory currentCategory = categoryService.findById(id);
		ShopflowCategory parentCategory;
		List<ShopflowCategory> brotherCategory;
		if (Objects.equals(currentCategory.getPid(),"0")) {
			parentCategory = currentCategory;
			brotherCategory = categoryService.queryByPid(currentCategory.getId());
			currentCategory = brotherCategory.size() > 0 ? brotherCategory.get(0) : currentCategory;
		} else {
			parentCategory = categoryService.findById(currentCategory.getPid());
			brotherCategory = categoryService.queryByPid(currentCategory.getPid());
		}
		GoodsCategoryResult result = new GoodsCategoryResult();
		result.setCurrentCategory(currentCategory);
		result.setParentCategory(parentCategory);
		result.setBrotherCategory(brotherCategory);
		return ResponseUtil.ok(result);
	}

	/**
	 * 根据条件搜素商品
	 * @return 根据条件搜素的商品详情
	 */
	public Object list(String userId, GoodsListBody body) {
		//添加到搜索历史
		if (userId != null && !Objects.isNull(body.getKeyword())) {
			ShopflowSearchHistory searchHistory = searchHistoryService.findByKeyword(userId, body.getKeyword());
			if (searchHistory != null){
				searchHistoryService.updateVersionSelective(searchHistory);
			} else {
				ShopflowSearchHistory searchHistoryVo = new ShopflowSearchHistory();
				searchHistoryVo.setKeyword(body.getKeyword());
				searchHistoryVo.setUserId(userId);
				searchHistoryVo.setFrom("wx");
				searchHistoryVo.setVersion(1);
				searchHistoryService.save(searchHistoryVo);
			}
		}

		//查询列表数据
		List<ShopflowGoods> goodsList = goodsService.querySelective(body);

		// 查询商品所属类目列表。
		List<String> goodsCatIds = goodsService.getCatIds(body.getBrandId(), body.getKeyword(), body.getIsHot(), body.getIsNew());
		List<ShopflowCategory> categoryList;
		if (goodsCatIds.size() != 0) {
			categoryList = categoryService.queryL2ByIds(goodsCatIds);
		} else {
			categoryList = new ArrayList<>(0);
		}

		PageInfo<ShopflowGoods> pagedList = PageInfo.of(goodsList);
		GoodsListResult result = new GoodsListResult();
		result.setList(goodsList);
		result.setTotal(pagedList.getTotal());
		result.setPage(pagedList.getPageNum());
		result.setLimit(pagedList.getPageSize());
		result.setPages(pagedList.getPages());
		result.setFilterCategoryList(categoryList);
		// 因为这里需要返回额外的filterCategoryList参数，因此不能方便使用ResponseUtil.okList
		return ResponseUtil.ok(result);
	}


	/**
	 * 商品详情页面“大家都在看”推荐商品
	 * @param goodId, 商品ID
	 * @return 商品详情页面推荐商品
	 */
	public Object related(String goodId) {
		ShopflowGoods goods = goodsService.findById(goodId);
		if (goods == null) {
			return ResponseUtil.badArgumentValue();
		}
		// 查找六个相关商品,优先级 分类 -> 店铺 -> 新品
		HashMap<String, ShopflowGoods> goodsMap = new HashMap<>();
		int related = 6;
		List<ShopflowGoods> goodsCategoryList = goodsService.queryByCategory(goods.getCategoryId(), related);
		for (ShopflowGoods g :goodsCategoryList) {
			if (goodsMap.size() < related){
				goodsMap.put(g.getId() , g);
			}
		}
		if (goodsMap.size() < related){
			List<ShopflowGoods> goodsBrandList = goodsService.queryByBrand(goods.getBrandId(), 10);
			for (ShopflowGoods g :goodsBrandList) {
				if (goodsMap.size() < related){
					goodsMap.put(g.getId() , g);
				}
			}
		}
		if (goodsMap.size() < related){
			List<ShopflowGoods> goodsNewList = goodsService.queryByNew(10);
			for (ShopflowGoods g :goodsNewList) {
				if (goodsMap.size() < related){
					goodsMap.put(g.getId() , g);
				}
			}
		}
		return ResponseUtil.okList(new ArrayList<>(goodsMap.values()));
	}

	/**
	 * 在售的商品总数
	 * @return 在售的商品总数
	 */
	public Object count() {
		return ResponseUtil.ok(goodsService.queryOnSale());
	}


	/**
	 * 评论数量
	 * @param goodsId 商品ID。
	 * @return 评论数量
	 */
	public Object commentCount(String goodsId) {
		GoodsCommentCountResult result = new GoodsCommentCountResult();
		result.setAllCount(goodsCommentService.count(goodsId, false));
		result.setHasPicCount(goodsCommentService.count(goodsId, true));
		return ResponseUtil.ok(result);
	}

	/**
	 * 评论列表
	 */
	public Object commentList(GoodsCommentListBody body) {
		List<ShopflowGoodsComment> commentList = goodsCommentService.querySelective(body);
		return ResponseUtil.okList(goodsCommentService.commentInfoList(commentList), commentList);
	}

}