package org.ysling.litemall.admin.model.democrud.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * CRUD 练习列表请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemoCrudListBody extends PageBody {

    /**
     * 问题关键字
     */
    private String question;
}
