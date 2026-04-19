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
import org.ysling.litemall.core.annotation.JsonBody;
import org.ysling.litemall.db.domain.LitemallAddress;
import org.ysling.litemall.wx.annotation.LoginUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.litemall.wx.web.impl.WxWebAddressService;
import javax.validation.Valid;

/**
 * 用户收货地址服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/address")
@Validated
public class WxAddressController {


	@Autowired
	private WxWebAddressService addressService;

	/**
	 * 用户收货地址列表
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	@GetMapping("list")
	public Object list(@LoginUser String userId) {
		return addressService.list(userId);
	}

	/**
	 * 获取租户地址
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	@GetMapping("tenant")
	public Object getAddress(@LoginUser String userId) {
		return addressService.getAddress(userId);
	}

	/**
	 * 收货地址详情
	 * @param userId 用户ID
	 * @param id     收货地址ID
	 * @return 收货地址详情
	 */
	@GetMapping("detail")
	public Object detail(@LoginUser String userId, @JsonBody String id) {
		return addressService.detail(userId , id);
	}

	/**
	 * 添加或更新收货地址
	 * @param userId  用户ID
	 * @param address 用户收货地址
	 * @return 添加或更新操作结果
	 */
	@PostMapping("save")
	public Object save(@LoginUser String userId, @Valid @RequestBody LitemallAddress address) {
		return addressService.save(userId , address);
	}

	/**
	 * 删除收货地址
	 * @param userId  用户ID
	 * @param id 	  用户收货地址ID
	 */
	@PostMapping("delete")
	public Object delete(@LoginUser String userId, @JsonBody String id) {
		return addressService.delete(userId , id);
	}

}