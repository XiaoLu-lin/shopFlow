package org.ysling.litemall.admin.model.log.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

/**
 * 日志列表请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogListBody extends PageBody {


    /**
     * 日志类型
     */
    private Short type;
    /**
     * 地址
     */
    private String name;
    /**
     * 日志状态
     */
    private Boolean status;



}
