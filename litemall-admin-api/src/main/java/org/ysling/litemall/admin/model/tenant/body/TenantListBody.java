package org.ysling.litemall.admin.model.tenant.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 多租户列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantListBody extends PageBody {


    /**
     * 租户名称
     */
    private String address;



}
