package org.ysling.litemall.admin.model.admin.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 管理员列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminListBody extends PageBody {

    /**
     * 邮箱
     */
    private String mail;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 账号名称
     */
    private String username;

}
