package org.ysling.litemall.wx.model.issue.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.db.entity.PageBody;
import org.ysling.litemall.core.annotation.NotValue;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IssueListBody extends PageBody implements Serializable {

    /**
     * 问题标题
     */
    private String question;

}
