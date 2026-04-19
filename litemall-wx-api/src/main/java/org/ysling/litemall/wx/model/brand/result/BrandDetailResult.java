package org.ysling.litemall.wx.model.brand.result;

import lombok.Data;
import org.ysling.litemall.db.domain.LitemallBrand;
import org.ysling.litemall.db.domain.LitemallUser;
import org.ysling.litemall.db.entity.UserInfo;

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
    private LitemallBrand brand;
    /**
     * 用户信息
     */
    private UserInfo brandUser;

}
