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
public class BrandListBody extends PageBody implements Serializable {

    /**
     * 店铺名称
     */
    private String brandName;


}
