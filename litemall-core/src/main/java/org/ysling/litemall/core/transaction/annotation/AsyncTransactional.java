package org.ysling.litemall.core.transaction.annotation;

import java.lang.annotation.*;

/**
 * @author Ysling
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncTransactional {

    /**
     * 是否是回调任务
     * 其他任务执行完后回调
     * @return true是，false不是
     */
    boolean value() default false;

}
