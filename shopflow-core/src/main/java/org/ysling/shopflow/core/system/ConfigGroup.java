package org.ysling.shopflow.core.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 枚举分组实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigGroup implements Serializable {

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组前缀
     */
    private String prefix;



}
