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
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.admin.model.profile.body.ProfilePasswordBody;
import org.ysling.shopflow.admin.model.profile.result.AdminUpdateBody;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.response.ResponseStatus;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.entity.AdminInfo;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.domain.ShopflowTenant;
import org.ysling.shopflow.admin.service.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Objects;

/**
 * 管理员个人账号管理
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/admin/profile")
@Validated
public class AdminProfileController {

    @Autowired
    private AdminAdminService adminService;
    @Autowired
    private AdminTenantService tenantService;

    /**
     * 修改密码
     */
    @SaCheckLogin
    @PostMapping("/password")
    public Object password(@Valid @RequestBody ProfilePasswordBody body) {
        String oldPassword = body.getOldPassword();
        String newPassword = body.getNewPassword();
        String confirmPassword = body.getConfirmPassword();

        if (!ObjectUtils.allNotNull(oldPassword,newPassword)){
            return ResponseUtil.badArgument();
        }

        if (newPassword == null || newPassword.length() < 6){
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0121);
        }

        if (!Objects.equals(newPassword, confirmPassword)){
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0134);
        }

        ShopflowAdmin admin = adminService.findById(StpUtil.getLoginIdAsString());
        if (!CryptoUtil.matches(oldPassword, admin.getPassword())) {
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0210);
        }

        admin.setPassword(CryptoUtil.encode(newPassword));
        if (adminService.updateVersionSelective(admin) == 0){
            throw new RuntimeException("网络繁忙,请重试");
        }
        return ResponseUtil.ok();
    }


    /**
     * 账号详情
     */
    @SaCheckLogin
    @GetMapping("/detail")
    public Object detail() {
        ShopflowAdmin admin = adminService.findById(StpUtil.getLoginIdAsString());
        AdminInfo info = new AdminInfo();
        BeanUtil.copyProperties(admin , info);
        ShopflowTenant tenant = tenantService.findById(admin.getTenantId());
        info.setTenant(tenant != null ? tenant.getAddress() : "平台管理员");
        return ResponseUtil.ok(info);
    }


    /**
     * 更新账号信息
     */
    @SaCheckLogin
    @PostMapping("/update")
    public Object update(@Valid @RequestBody AdminUpdateBody body) {
        ShopflowAdmin admin = new ShopflowAdmin();
        admin.setId(StpUtil.getLoginIdAsString());
        BeanUtil.copyProperties(body, admin);
        if (adminService.updateVersionSelective(admin) == 0){
            throw new RuntimeException("网络繁忙,请重试");
        }
        return ResponseUtil.ok();
    }

}
