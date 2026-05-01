package org.ysling.shopflow.admin.model.storage.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;
import java.io.Serializable;

/**
 * 对象存储列表请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageListBody extends PageBody implements Serializable {

    /**
     * 对象存储key
     */
    private String key;

    /**
     * 对象名称
     */
    private String name;

}
