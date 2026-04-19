package org.ysling.litemall.wx.model.user.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallUser;
import org.ysling.litemall.db.entity.UserInfo;
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
