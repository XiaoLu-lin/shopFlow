package org.ysling.shopflow.admin.model.log.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;
import java.io.Serializable;

/**
 * 日志查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogSystemListBody extends PageBody implements Serializable {

    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件路径
     */
    private String filePath = "/logs";


}
