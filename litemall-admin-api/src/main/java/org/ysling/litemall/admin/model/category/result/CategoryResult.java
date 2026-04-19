package org.ysling.litemall.admin.model.category.result;


import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分类列表响应结果
 * @author Ysling
 */
@Data
public class CategoryResult implements Serializable {

    /**
     * 分类id
     */
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 关键字
     */
    private String keywords;
    /**
     * 排序字段
     */
    private String desc;
    /**
     * 分类图标
     */
    private String iconUrl;
    /**
     * 分类图片
     */
    private String picUrl;
    /**
     * 分类等级
     */
    private String level;
    /**
     * 权重用于排序
     */
    private Integer weight;
    /**
     * 子分类列表
     */
    private List<CategoryResult> children;

}
