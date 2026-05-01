package org.ysling.shopflow.wx.model.user.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.UserInfo;
import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class UserShareResult implements Serializable {

    /**
     * 用户信息
     */
    private UserInfo userShare;

    /**
     * 已邀请用户
     */
    private List<UserInfo> inviterList;

}
