package org.ysling.litemall.wx.model.brand.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandOrderListBody extends PageBody implements Serializable {

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 查看类型
     */
    private Integer showType = 0;


}
