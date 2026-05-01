package org.ysling.shopflow.core.system.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.annotation.SystemEnums;
import java.io.Serializable;

/**
 * 商城默认配置枚举
 * @author Ysling
 */
@Getter
@SystemEnums(name = "商城配置" , prefix = "mall")
@JsonFormat(shape = JsonFormat.Shape.OBJECT) //将枚举类型序列化为对象
public enum SystemMallConfig implements Serializable {

    /////////////////////////////////
    //       商城配置
    ////////////////////////////////

    MALL_QQ("mall_qq" ,"927069313","","商城QQ交流群", String.class),
    MALL_NAME("mall_name" ,"天天云游市场","","商城名称", String.class),
    MALL_ADDRESS("mall_address" ,"贵阳","","商城地址", String.class),
    MALL_PHONE("mall_phone" ,"185-8567-5204","","商城联系电话", String.class),
    MALL_LATITUDE("mall_latitude" ,"31.201900","","商城纬度", String.class),
    MALL_LONGITUDE("mall_longitude","121.587839","","商城经度", String.class);

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
    SystemMallConfig(String name, String value, String unit, String depict, Class<?> objectType) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.depict = depict;
        this.objectType = objectType;
    }

    /**商城名称*/
    public static String getMallName() {
        return SystemUtils.getConfig(MALL_NAME.getName());
    }

    /**商城地址*/
    public static String getMallAddress() {
        return SystemUtils.getConfig(MALL_ADDRESS.getName());
    }

    /**商城联系电话*/
    public static String getMallPhone() {
        return SystemUtils.getConfig(MALL_PHONE.getName());
    }

    /**商城QQ交流群*/
    public static String getMallQQ() {
        return SystemUtils.getConfig(MALL_QQ.getName());
    }

    /**商城经度*/
    public static String getMallLongitude() {
        return SystemUtils.getConfig(MALL_LONGITUDE.getName());
    }

    /**商城纬度*/
    public static String getMallLatitude() {
        return SystemUtils.getConfig(MALL_LATITUDE.getName());
    }

}
