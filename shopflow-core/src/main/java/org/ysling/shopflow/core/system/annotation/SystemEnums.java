package org.ysling.shopflow.core.system.annotation;

import java.lang.annotation.*;

/**
 * 系统枚举
 * @author Ysling
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemEnums {

    /**
     * 配置名
     */
    String name() default "系统配置";

    /**
     * 配置前缀
     */
    String prefix() default "system";

}
