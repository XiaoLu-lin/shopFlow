package org.ysling.litemall.admin.model.user.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

import java.io.Serializable;

/**
 * 用户列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserListBody extends PageBody implements Serializable {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 联系电话
     */
    private String mobile;
    /**
     * 用户邀请者
     */
    private String inviter;
    /**
     * 用户等级
     */
    private Byte level;

}
