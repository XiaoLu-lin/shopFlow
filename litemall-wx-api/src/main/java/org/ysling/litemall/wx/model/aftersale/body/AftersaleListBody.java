package org.ysling.litemall.wx.model.aftersale.body;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.litemall.db.entity.PageBody;
import java.io.Serializable;

/**
 * 售后请求参数
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AftersaleListBody extends PageBody implements Serializable {

    /**
     * 售后状态
     */
    private Short status;


}
