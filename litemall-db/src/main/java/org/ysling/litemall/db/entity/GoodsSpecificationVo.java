package org.ysling.litemall.db.entity;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallGoodsSpecification;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ysling
 */
@Data
public class GoodsSpecificationVo implements Serializable {

    /**
     * 规格名
     */
    private String name;
    /**
     * 规格列表
     */
    private List<LitemallGoodsSpecification> valueList;


}
