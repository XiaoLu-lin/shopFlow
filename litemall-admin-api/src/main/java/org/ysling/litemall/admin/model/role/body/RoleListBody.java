package org.ysling.litemall.admin.model.role.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

import java.io.Serializable;

/**
 * 角色集合请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleListBody extends PageBody implements Serializable {

    /**
     * 角色名称
     */
    private String name;


}
