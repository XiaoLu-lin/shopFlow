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
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ysling.shopflow.admin.annotation.RequiresPermissionsDesc;
import org.ysling.shopflow.admin.model.user.body.DealingSlipListBody;
import org.ysling.shopflow.admin.model.user.body.UserListBody;
import org.ysling.shopflow.admin.model.user.result.DealingSlipListResult;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.weixin.enums.FailReasonStatus;
import org.ysling.shopflow.core.weixin.enums.TransferStatus;
import org.ysling.shopflow.db.domain.ShopflowDealingSlip;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.admin.service.AdminDealingSlipService;
import org.ysling.shopflow.admin.service.AdminUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.shopflow.db.enums.DealType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;


/**
 * 用户管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {

    @Autowired
    private AdminUserService userService;
    @Autowired
    private AdminDealingSlipService dealingSlipService;


    /**
     * 用户查询
     */
    @SaCheckPermission("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(UserListBody body) {
        return ResponseUtil.okList(userService.querySelective(body));
    }

    /**
     * 用户详情
     * @param id 用户ID
     */
    @SaCheckPermission("admin:user:detail")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "详情")
    @GetMapping("/detail")
    public Object userDetail(@JsonBody String id) {
        return ResponseUtil.ok(userService.findById(id));
    }

    /**
     * 用户编辑
     */
    @SaCheckPermission("admin:user:update")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object userUpdate(@Valid @RequestBody ShopflowUser user) {
        user.setIntegral(null);
        if (userService.updateVersionSelective(user) <= 0){
            throw new RuntimeException("用户余额更新失败,请重试");
        }
        return ResponseUtil.ok();
    }

    /**
     * 用户批量上传
     */
    @SaCheckPermission("admin:user:upload")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "用户上传")
    @PostMapping("/upload")
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<ShopflowUser> userList = reader.readAll(ShopflowUser.class);
        for (ShopflowUser user :userList) {
            if (!userService.saveOrUpdate(user)){
                throw new RuntimeException(String.format("用户ID(%s)更新失败", user.getId()));
            }
        }
        return ResponseUtil.ok();
    }

    /**
     * 用户交易记录
     */
    @SaCheckPermission("admin:user:deal-list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "交易记录")
    @GetMapping("/deal-list")
    public Object tradingRecord(DealingSlipListBody body) {
        if (Objects.isNull(body.getUserId())) {
            return ResponseUtil.fail("用户不存在");
        }
        ShopflowUser user = userService.findById(body.getUserId());
        if (user == null) {
            return ResponseUtil.fail("用户不存在");
        }
        List<ShopflowDealingSlip> dealingSlipList = dealingSlipService.querySelective(body);
        ArrayList<DealingSlipListResult> listResults = new ArrayList<>();
        for (ShopflowDealingSlip dealingSlip :dealingSlipList) {
            DealingSlipListResult result = new DealingSlipListResult();
            BeanUtil.copyProperties(dealingSlip , result);
            result.setDealTypeText(DealType.parseValue(dealingSlip.getDealType()));
            String value = FailReasonStatus.parseValue(dealingSlip.getRemark());
            if (StringUtils.hasText(value)){
                result.setStatusText(value);
            }else {
                result.setStatusText(TransferStatus.parseValue(dealingSlip.getStatus()));
            }
            listResults.add(result);
        }
        return ResponseUtil.okList(listResults , dealingSlipList);
    }


}
