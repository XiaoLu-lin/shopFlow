package org.ysling.litemall.db.validator.sort;
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * 自定义参数校验注解
 */
public class SortValidator implements ConstraintValidator<Sort, String> {

    private List<String> valueList;

    /**
     * 注解初始化
     * @param sort 注解
     */
    @Override
    public void initialize(Sort sort) {
        valueList = Lists.newArrayList();
        for (String val : sort.accepts()) {
            valueList.add(val.toUpperCase());
        }
    }

    /**
     * 参数校验
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return valueList.contains(s.toUpperCase());
    }
}
