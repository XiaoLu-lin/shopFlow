package org.ysling.litemall.admin.model.notice.admin.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 管理员通知列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeAdminListBody extends PageBody {

    /**
     * 通知管理员
     */
    private String adminId;
    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知类型
     */
    private String type;

}
