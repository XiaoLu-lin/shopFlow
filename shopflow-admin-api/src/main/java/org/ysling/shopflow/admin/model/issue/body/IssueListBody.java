package org.ysling.shopflow.admin.model.issue.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

/**
 * 通用问题列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IssueListBody extends PageBody {

    /**
     * 问题标题
     */
    private String question;


}
