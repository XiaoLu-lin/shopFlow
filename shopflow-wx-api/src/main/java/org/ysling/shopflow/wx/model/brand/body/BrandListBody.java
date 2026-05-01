package org.ysling.shopflow.wx.model.brand.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;
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
