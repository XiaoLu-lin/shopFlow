package org.ysling.litemall.admin.model.notice.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallNotice;
import org.ysling.litemall.db.domain.LitemallNoticeAdmin;

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
    private LitemallNotice notice;

    /**
     * 通知管理员信息
     */
    private List<LitemallNoticeAdmin> noticeAdminList;

}
