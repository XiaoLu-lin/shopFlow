package org.ysling.litemall.core.transaction.annotation;

import java.lang.annotation.*;

/**
 * @author Ysling
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MainAsyncTransactional {

}
