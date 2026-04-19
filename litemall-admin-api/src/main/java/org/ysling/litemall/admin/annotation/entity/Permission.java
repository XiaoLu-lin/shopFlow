package org.ysling.litemall.admin.annotation.entity;
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
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.ysling.litemall.admin.annotation.RequiresPermissionsDesc;
import java.io.Serializable;

/**
 * 权限实体类
 * @author Ysling
 */
public class Permission implements Serializable {

    private String api;
    private SaCheckPermission saCheckPermission;
    private RequiresPermissionsDesc requiresPermissionsDesc;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public SaCheckPermission getSaCheckPermission() {
        return saCheckPermission;
    }

    public void setSaCheckPermission(SaCheckPermission saCheckPermission) {
        this.saCheckPermission = saCheckPermission;
    }

    public RequiresPermissionsDesc getRequiresPermissionsDesc() {
        return requiresPermissionsDesc;
    }

    public void setRequiresPermissionsDesc(RequiresPermissionsDesc requiresPermissionsDesc) {
        this.requiresPermissionsDesc = requiresPermissionsDesc;
    }
}
