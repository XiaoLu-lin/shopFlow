package org.ysling.shopflow.admin.model.notice.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowNotice;
import org.ysling.shopflow.db.domain.ShopflowNoticeAdmin;

import java.io.Serializable;
import java.util.List;

/**
 * 通知详情
 * @author Ysling
 */
@Data
public class NoticeReadResult implements Serializable {

    /**
     * 通知信息
     */
    private ShopflowNotice notice;

    /**
     * 通知管理员信息
     */
    private List<ShopflowNoticeAdmin> noticeAdminList;

}
