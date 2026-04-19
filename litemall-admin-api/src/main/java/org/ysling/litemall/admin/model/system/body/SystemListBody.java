package org.ysling.litemall.admin.model.system.body;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置列表请求参数
 */
@Data
public class SystemListBody implements Serializable {

    /**
     * 系统配置名
     */
    private String name;

}
