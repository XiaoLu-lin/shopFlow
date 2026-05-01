package org.ysling.shopflow.admin.model.role.body;

import lombok.Data;

import org.ysling.shopflow.core.annotation.NotValue;
import java.io.Serializable;
import java.util.List;

/**
 * 批量更新权限请求参数
 * @author Ysling
 */
@Data
public class RoleUpdatePermissionsBody implements Serializable {

    /**
     * 角色id
     */
    @NotValue(message = "角色id不能为空")
    private String roleId;
    /**
     * 权限列表
     */
    @NotValue(message = "权限列表不能为空")
    private List<String> permissions;

}
