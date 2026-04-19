package org.ysling.litemall.core.system.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.system.annotation.SystemEnums;
import java.io.Serializable;

/**
 * 微信默认配置枚举
 *
 * @author Ysling
 */
@Getter
@SystemEnums(name = "微信配置", prefix = "wx")
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举类型序列化为对象
public enum SystemWxConfig implements Serializable {

    /////////////////////////////////
    //       微信配置
    ////////////////////////////////

    WX_SYSTEM_PAY("wx_system_pay", "true", "", "使用微信支付", Boolean.class),
    WX_SYSTEM_SHARE("wx_system_share", "true", "", "自动创建朋友圈分享图", Boolean.class),
    WX_INDEX_COUPON("wx_index_coupon", "3", "条", "首页优惠券展示条数", Integer.class),
    WX_INDEX_NEW("wx_index_new", "20", "条", "首页新品条数", Integer.class),
    WX_INDEX_HOT("wx_index_hot", "20", "条", "首页热卖条数", Integer.class),
    WX_INDEX_ALL("wx_index_all", "20", "条", "首页分页商品条数", Integer.class),
    WX_INDEX_REWARD("wx_index_reward", "4", "条", "首页赏金商品条数", Integer.class),
    WX_INDEX_GROUPON("wx_index_groupon", "4", "条", "首页团购商品条数", Integer.class);

    /**
     * 配置名
     */
    private final String name;
    /**
     * 配置值
     */
    private final String value;
    /**
     * 配置单位
     */
    private final String unit;
    /**
     * 配置描述
     */
    private final String depict;
    /**
     * 配置描述
     */
    private final Class<?> objectType;

    /**
     * 构造方法
     */
    SystemWxConfig(String name, String value, String unit, String depict, Class<?> objectType) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.depict = depict;
        this.objectType = objectType;
    }



    /**
     * 首页新品商品展示数量
     */
    public static Integer getNewLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_NEW.getName());
    }

    /**
     * 首页热卖商品展示数量
     */
    public static Integer getHotLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_HOT.getName());
    }

    /**
     * 首页懒加载商品展示数量
     */
    public static Integer getAllLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_ALL.getName());
    }

    /**
     * 首页赏金商品展示数量
     */
    public static Integer getRewardLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_REWARD.getName());
    }

    /**
     * 首页赏金商品展示数量
     */
    public static Integer getGrouponLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_GROUPON.getName());
    }

    /**
     * 首页优惠券展示数量
     */
    public static Integer getCouponLimit() {
        return SystemUtils.getConfigInt(WX_INDEX_COUPON.getName());
    }

    /**
     * 使用微信支付
     */
    public static boolean isAutoPay() {
        return SystemUtils.getConfigBoolean(WX_SYSTEM_PAY.getName());
    }

    /**
     * 自动创建朋友圈分享图
     */
    public static boolean isAutoCreateShareImage() {
        return SystemUtils.getConfigBoolean(WX_SYSTEM_SHARE.getName());
    }


}
