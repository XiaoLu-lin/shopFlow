package org.ysling.shopflow.wx.model.home.result;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.ysling.shopflow.db.domain.*;

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
    private List<ShopflowAd> banner;
    /**
     * 分类列表
     */
    private List<ShopflowCategory> channel;
    /**
     * 优惠券列表
     */
    private List<ShopflowCoupon> couponList;
    /**
     * 新品商品
     */
    private List<ShopflowGoods> newGoodsList;
    /**
     * 热卖商品
     */
    private List<ShopflowGoods> hotGoodsList;
    /**
     * 所有商品第一页
     */
    private PageInfo<ShopflowGoods> allGoodsList;
    /**
     * 赏金规则列表
     */
    private List<ShopflowRewardTask> rewardTaskList;
    /**
     * 团购规则列表
     */
    private List<ShopflowGrouponRules> grouponRuleList;

}
