package org.ysling.litemall.wx.model.home.result;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.ysling.litemall.db.domain.*;

import java.io.Serializable;
import java.util.List;

/**
 * 首页信息
 * @author Ysling
 */
@Data
public class HomeIndexResult implements Serializable {

    /**
     * 多租户token
     */
    private String tenantId;
    /**
     * 广告列表
     */
    private List<LitemallAd> banner;
    /**
     * 分类列表
     */
    private List<LitemallCategory> channel;
    /**
     * 优惠券列表
     */
    private List<LitemallCoupon> couponList;
    /**
     * 新品商品
     */
    private List<LitemallGoods> newGoodsList;
    /**
     * 热卖商品
     */
    private List<LitemallGoods> hotGoodsList;
    /**
     * 所有商品第一页
     */
    private PageInfo<LitemallGoods> allGoodsList;
    /**
     * 赏金规则列表
     */
    private List<LitemallRewardTask> rewardTaskList;
    /**
     * 团购规则列表
     */
    private List<LitemallGrouponRules> grouponRuleList;

}
