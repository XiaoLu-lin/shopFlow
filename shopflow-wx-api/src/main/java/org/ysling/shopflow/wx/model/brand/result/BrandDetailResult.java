package org.ysling.shopflow.wx.model.brand.result;

import lombok.Data;
import org.ysling.shopflow.db.domain.ShopflowBrand;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.UserInfo;

import java.io.Serializable;

/**
 * @author Ysling
 */
@Data
public class BrandDetailResult implements Serializable {

    /**
     * 是否点赞
     */
    private Boolean brandLike;
    /**
     * 店铺信息
     */
    private ShopflowBrand brand;
    /**
     * 用户信息
     */
    private UserInfo brandUser;

}
