package org.ysling.litemall.core.system.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.ysling.litemall.core.system.SystemUtils;
import org.ysling.litemall.core.system.annotation.SystemEnums;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单默认配置枚举
 * @author Ysling
 */
@Getter
@SystemEnums(name = "订单配置" , prefix = "order")
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举类型序列化为对象
public enum SystemOrderConfig implements Serializable {

    /////////////////////////////////
    //       订单配置
    ////////////////////////////////

    ORDER_UNPAID("order_unpaid" ,"30","分钟","订单支付超时时间" , Integer.class),
    ORDER_COMMENT("order_comment" ,"7","天","订单评论超时时间" , Integer.class),
    ORDER_UNCONFIRMED("order_unconfirmed" ,"7","天","订单确认收货超时时间" , Integer.class),
    ORDER_FREIGHT_VALUE("order_freight_value" ,"0","元","运费金额", Double.class),
    ORDER_EXPRESS_FREIGHT_MIN("order_express_freight_min" ,"0","元","运费减免最小金额", Double.class);

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
    SystemOrderConfig(String name, String value, String unit, String depict, Class<?> objectType) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.depict = depict;
        this.objectType = objectType;
    }

    /**运费减免最小金额 单位元*/
    public static BigDecimal getFreightMin() {
        return SystemUtils.getConfigBigDec(ORDER_EXPRESS_FREIGHT_MIN.getName());
    }


    /**运费金额 单位元*/
    public static BigDecimal getFreightValue() {
        return SystemUtils.getConfigBigDec(ORDER_FREIGHT_VALUE.getName());
    }

    /**支付超时时间 单位分钟*/
    public static Integer getOrderUnpaid() {
        return SystemUtils.getConfigInt(ORDER_UNPAID.getName());
    }

    /**确认收货超时时间 单位天*/
    public static Integer getOrderUnconfirmed() {
        return SystemUtils.getConfigInt(ORDER_UNCONFIRMED.getName());
    }

    /**评论超时时间 单位天*/
    public static Integer getOrderComment() {
        return SystemUtils.getConfigInt(ORDER_COMMENT.getName());
    }

}
