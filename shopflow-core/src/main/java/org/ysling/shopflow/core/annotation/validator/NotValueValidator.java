package org.ysling.shopflow.core.annotation.validator;
// Copyright (c) [ysling] [927069313@qq.com]
// [naonao-jub] is licensed under Mulan PSL v2.
// You can use this software according to the terms and conditions of the Mulan PSL v2.
// You may obtain a copy of Mulan PSL v2 at:
//             http://license.coscl.org.cn/MulanPSL2
// THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// See the Mulan PSL v2 for more details.


import com.beust.jcommander.internal.Lists;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.annotation.NotValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自定义参数校验注解
 * @author Ysling
 */
public class NotValueValidator implements ConstraintValidator<NotValue, Object> {

    /**
     * 待校验参数
     */
    private List<Object> valueList;

    /**
     * 注解初始化
     * @param notValue 注解
     */
    @Override
    public void initialize(NotValue notValue) {
        valueList = Lists.newArrayList();
        valueList.addAll(Arrays.asList(notValue.accepts()));
    }

    /**
     * 参数校验
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null){
            return false;
        }
        if (!valueList.isEmpty()){
            if (!valueList.contains(value)){
                return false;
            }
        }
        if (value instanceof Map){
            return ((Map<?,?>)value).size() > 0;
        }
        if (value instanceof String){
            return StringUtils.hasText((String) value);
        }
        if (value instanceof Collection){
            return ((Collection<?>)value).size() > 0;
        }
        if (value instanceof BigDecimal){
            return ((BigDecimal)value).compareTo(BigDecimal.ZERO) >= 0;
        }
        return true;
    }


}
