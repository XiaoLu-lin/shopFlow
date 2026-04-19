package org.ysling.litemall.wx.model.message.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryMessageBody extends PageBody implements Serializable {

    /**
     * 消息接收者
     */
    @NotValue(message = "消息接收者不能为空")
    private String receiveUserId;

}
