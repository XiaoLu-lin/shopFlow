package org.ysling.shopflow.wx.model.brand.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageBody;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandGoodsListBody extends PageBody implements Serializable {

    /**
     * 店铺ID
     */
    private String brandId;


}
