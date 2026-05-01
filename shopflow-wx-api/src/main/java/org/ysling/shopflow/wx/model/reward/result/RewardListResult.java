package org.ysling.shopflow.wx.model.reward.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ysling.shopflow.db.entity.PageResult;

import java.math.BigDecimal;

/**
 * @author Ysling
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RewardListResult<T> extends PageResult<T> {

    /**
     * 收益
     */
    private BigDecimal earnings;
    /**
     * 余额
     */
    private BigDecimal balance;

}
