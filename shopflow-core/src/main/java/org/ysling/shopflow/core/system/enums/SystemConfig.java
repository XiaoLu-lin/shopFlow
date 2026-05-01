package org.ysling.shopflow.core.system.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.annotation.SystemEnums;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 系统默认配置枚举
 * @author Ysling
 */
@Getter
@SystemEnums(name = "系统配置" , prefix = "system")
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举类型序列化为对象
public enum SystemConfig implements Serializable {

    /////////////////////////////////
    //       系统配置
    ////////////////////////////////

    SYSTEM_AUTH_WX_PAY("system_auth_wx_pay" ,"true","","是否开启微信支付", Boolean.class),
    SYSTEM_SHARE_FONT("system_share_font" ,"Microsoft YaHei","","分享图字体类型", String.class),
    SYSTEM_DES_FILE_SIZE("system_des_file_size" ,"1024","KB","指定图片压缩大小", Integer.class),
    SYSTEM_SHARE_IMAGE("system_share_image" ,"true","","自动创建朋友圈分享图", Boolean.class),
    SYSTEM_ORDER_BROKERAGE("system_order_brokerage" ,"6" ,"%","订单服务费默认比例", Integer.class),
    SYSTEM_GOODS_MIN_AMOUNT("system_goods_min_amount" ,"1" ,"元","商品可设置的最小金额", Integer.class),
    SYSTEM_GOODS_MAX_AMOUNT("system_goods_max_amount" ,"1000" ,"元","商品可设置的最大金额", Integer.class);

    /**配置名*/
    private final String name;
    /**配置值*/
    private final String value;
    /**配置单位*/
    private final String unit;
    /**配置描述*/
    private final String depict;
    /**配置描述*/
    private final Class<?> objectType;

    /**构造方法*/
    SystemConfig(String name, String value, String unit, String depict, Class<?> objectType) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.depict = depict;
        this.objectType = objectType;
    }

    /**使用微信支付*/
    public static boolean isAutoWxPay() {
        return SystemUtils.getConfigBoolean(SystemConfig.SYSTEM_AUTH_WX_PAY.getName());
    }

    /**分享图字体类型*/
    public static String getShareFont(){
        return SystemUtils.getConfig(SYSTEM_SHARE_FONT.getName());
    }

    /**指定图片压缩大小*/
    public static Integer getDesFileSize() {
        return SystemUtils.getConfigInt(SYSTEM_DES_FILE_SIZE.getName());
    }

    /**自动创建朋友圈分享图*/
    public static boolean isAutoCreateShareImage() {
        return SystemUtils.getConfigBoolean(SYSTEM_SHARE_IMAGE.getName());
    }

    /**订单服务费比例 % */
    public static BigDecimal getOrderBrokerage() {
        return SystemUtils.getConfigBigDec(SYSTEM_ORDER_BROKERAGE.getName());
    }

    /**商品可设置的最小金额 单位元*/
    public static Integer getGoodsMinAmount() {
        return SystemUtils.getConfigInt(SYSTEM_GOODS_MIN_AMOUNT.getName());
    }

    /**商品可设置的最大金额 单位元*/
    public static Integer getGoodsMaxAmount() {
        return SystemUtils.getConfigInt(SYSTEM_GOODS_MAX_AMOUNT.getName());
    }

}

