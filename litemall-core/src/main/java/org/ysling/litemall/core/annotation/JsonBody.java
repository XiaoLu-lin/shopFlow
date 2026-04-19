package org.ysling.litemall.core.annotation;


import java.lang.annotation.*;

/**
 * 获取请求中某个属性的值
 * @author Ysling
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonBody {

    /**
     * 值名称
     */
    String value() default "";

    /**
     * 是否必须
     */
    boolean require() default true;

}
