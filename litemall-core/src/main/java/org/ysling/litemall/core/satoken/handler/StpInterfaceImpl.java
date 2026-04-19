package org.ysling.litemall.core.satoken.handler;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ysling.litemall.core.service.CommonService;
import org.ysling.litemall.core.tenant.handler.TenantContextHolder;
import org.ysling.litemall.db.domain.LitemallAdmin;
import org.ysling.litemall.db.service.IAdminService;
import java.util.*;

/**
 * 自定义权限验证接口扩展
 * 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
 * @author Ysling
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private IAdminService adminService;
    @Autowired
    private CommonService commonService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LitemallAdmin admin = adminService.findById(loginId.toString());
        if (admin == null){
            return new ArrayList<>();
        }
        String[] roleIds = admin.getRoleIds();
        return new ArrayList<>(commonService.queryByRoleIds(roleIds));
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LitemallAdmin admin = adminService.findById(loginId.toString());
        if (admin == null){
            return new ArrayList<>();
        }
        String[] roleIds = admin.getRoleIds();
        return new ArrayList<>(commonService.queryByIds(roleIds));
    }



}
