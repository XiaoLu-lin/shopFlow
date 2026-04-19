package org.ysling.litemall.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 选择列表
 * @author Ysling
 */
@Data
public class BaseOption implements Serializable {

    /**
     * 选择值
     */
    private Object value;

    /**
     * 选择标签
     */
    private String label;

    /**
     * 子选择列表
     */
    private List<?> children;

}
