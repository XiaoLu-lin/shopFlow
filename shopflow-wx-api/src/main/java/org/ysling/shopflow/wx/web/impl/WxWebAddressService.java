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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowAddress;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.wx.service.WxAddressService;
import org.ysling.shopflow.wx.service.WxTenantService;
import org.ysling.shopflow.wx.service.WxUserService;
import java.util.Objects;

/**
 * 用户收货地址服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebAddressService {

	@Autowired
	private WxAddressService addressService;
	@Autowired
    private WxTenantService tenantService;
	@Autowired
	private WxUserService userService;


	/**
	 * 用户收货地址列表
	 *
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	public Object list(String userId) {
		return ResponseUtil.okList(addressService.queryByUid(userId));
	}

	/**
	 * 获取租户地址
	 *
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	public Object getAddress(String userId) {
		ShopflowUser user = userService.findById(userId);
		if (user == null){
			return ResponseUtil.fail("用户获取失败，请重新登陆");
		}
		return ResponseUtil.ok(tenantService.findById(user.getTenantId()));
	}

	/**
	 * 收货地址详情
	 *
	 * @param userId 用户ID
	 * @param id     收货地址ID
	 * @return 收货地址详情
	 */
	public Object detail(String userId, String id) {
		if (Objects.isNull(userId)) {
			return ResponseUtil.unlogin();
		}
		ShopflowAddress address = addressService.query(userId, id);
		if (address == null) {
			return ResponseUtil.fail("地址获取失败");
		}
		return ResponseUtil.ok(address);
	}

	/**
	 * 添加或更新收货地址
	 *
	 * @param userId  用户ID
	 * @param address 用户收货地址
	 * @return 添加或更新操作结果
	 */
	public Object save(String userId, ShopflowAddress address) {
		if (Objects.isNull(userId)) {
			return ResponseUtil.unlogin();
		}
		Object error = validate(address);
		if (error != null) {
			return error;
		}
		if (address.getId() == null || "0".equals(address.getId())) {
			if (address.getIsDefault()) {
				// 重置其他收货地址的默认选项
				addressService.resetDefault(userId);
			}
			address.setId(null);
			address.setUserId(userId);
			addressService.add(address);
		} else {
			ShopflowAddress shopflowAddress = addressService.query(userId, address.getId());
			if (shopflowAddress == null) {
				return ResponseUtil.badArgumentValue();
			}
			if (address.getIsDefault()) {
				// 重置其他收货地址的默认选项
				addressService.resetDefault(userId);
			}
			address.setUserId(userId);
			if (addressService.updateSelective(address) <= 0){
				throw new RuntimeException("网络繁忙，请刷新重试");
			}
		}
		return ResponseUtil.ok(address.getId());
	}

	/**
	 * 删除收货地址
	 *
	 * @param userId  用户ID
	 * @param id 	  用户收货地址ID
	 */
	public Object delete(String userId, String id) {
		if (Objects.isNull(userId)) {
			return ResponseUtil.unlogin();
		}
		addressService.deleteByUser(userId, id);
		return ResponseUtil.ok();
	}


	private Object validate(ShopflowAddress address) {
		String name = address.getName();
		if (!StringUtils.hasText(name)) {
			return ResponseUtil.badArgument();
		}

		// 测试收货手机号码是否正确
		String mobile = address.getMobile();
		if (!StringUtils.hasText(mobile) || !RegexUtil.isMobileSimple(mobile)) {
			return ResponseUtil.badArgument();
		}

		String province = address.getProvince();
		if (Objects.isNull(province)) {
			return ResponseUtil.badArgument();
		}

		String city = address.getCity();
		if (Objects.isNull(city)) {
			return ResponseUtil.badArgument();
		}

		String county = address.getCounty();
		if (Objects.isNull(county)) {
			return ResponseUtil.badArgument();
		}

		String areaCode = address.getAreaCode();
		if (Objects.isNull(areaCode)) {
			return ResponseUtil.badArgument();
		}

		String addressDetail = address.getAddressDetail();
		if (!StringUtils.hasText(addressDetail)) {
			return ResponseUtil.badArgument();
		}

		String addressAll = address.getAddressAll();
		if (!StringUtils.hasText(addressAll)) {
			return ResponseUtil.badArgument();
		}

		Boolean isDefault = address.getIsDefault();
		if (Objects.isNull(isDefault)) {
			return ResponseUtil.badArgument();
		}
		return null;
	}


}