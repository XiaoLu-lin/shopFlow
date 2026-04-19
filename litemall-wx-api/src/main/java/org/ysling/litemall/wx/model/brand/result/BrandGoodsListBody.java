package org.ysling.litemall.wx.model.brand.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;

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
