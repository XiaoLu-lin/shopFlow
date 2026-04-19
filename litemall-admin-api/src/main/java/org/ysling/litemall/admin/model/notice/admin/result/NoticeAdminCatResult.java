package org.ysling.litemall.admin.model.notice.admin.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Ysling
 */
@Data
public class NoticeAdminCatResult implements Serializable {

    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知日期
     */
    private LocalDateTime time;
    /**
     * 发送人
     */
    private String admin;
    /**
     * 通知头像
     */
    private String avatar;

}
