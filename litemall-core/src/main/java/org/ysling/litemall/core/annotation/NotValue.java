package org.ysling.litemall.core.annotation;
// Copyright (c) [ysling] [927069313@qq.com]
// [naonao-jub] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.

import org.ysling.litemall.core.annotation.validator.NotValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义参数校验
 * @author Ysling
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NotValueValidator.class)
public @interface NotValue {

    /**
     * 错误提示信息
     */
    String message() default "参数不能为空";

    /**
     * 符合校验条件的参数列表，不为空时参数必须为列表中的某个参数
     */
    String[] accepts() default { };

    /**
     * 默认为空数组，表示不使用组
     */
    Class<?>[] groups() default { };

    /**
     * 默认为空数组，不使用有效载荷
     */
    Class<? extends Payload>[] payload() default { };

}
