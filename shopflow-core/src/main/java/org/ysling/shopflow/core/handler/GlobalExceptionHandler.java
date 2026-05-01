package org.ysling.shopflow.core.handler;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

/**
 * 全局异常配置类
 * @author Ysling
 */
@Order
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Object seriousHandler(Exception e) {
        log.error(e.getMessage(), e);
        ActionLogHandler.logGeneralFail("Exception",e.getMessage());
        return ResponseUtil.fail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Object runtimeHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.fail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public Object bindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(n -> String.format("%s: %s", n.getField(), n.getDefaultMessage()))
                .reduce((x, y) -> String.format("%s; %s", x, y))
                .orElse("参数输入有误");
        log.error(msg);
        return ResponseUtil.badArgument();
    }

    @ResponseBody
    @ExceptionHandler(NotLoginException.class)
    public Object notLoginException(NotLoginException e) {
        log.error(e.getMessage());
        return ResponseUtil.unlogin();
    }

    @ResponseBody
    @ExceptionHandler(RedisSystemException.class)
    public Object redisSystemException(RedisSystemException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.fail("网络繁忙，请刷新重试");
    }

    @ResponseBody
    @ExceptionHandler(NullPointerException.class)
    public Object nullPointerHandler(NullPointerException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.fail("信息获取失败");
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public Object badArgumentHandler(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object badArgumentHandler(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }


    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object badArgumentHandler(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }


    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object badArgumentHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }


    /**
     * 数据校验全局处理
     * MethodArgumentNotValidException是@RequestBody和@Validated配合时产生的异常，比如在传参时如果前端的json数据里部分缺失@RequestBody修饰的实体类的属性就会产生这个异常。
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        //获取实体类定义的校验注解字段上的message作为异常信息，@NotBlank(message = "用户密码不能为空！")异常信息即为"用户密码不能为空！"
        log.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError == null){
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.fail(fieldError.getDefaultMessage());
    }


    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public Object badArgumentHandler(ValidationException e) {
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                String message = ((PathImpl) item.getPropertyPath()).getLeafNode().getName() + item.getMessage();
                log.error(message, e);
                return ResponseUtil.fail(402, message);
            }
        }
        log.error(e.getMessage(), e);
        return ResponseUtil.badArgumentValue();
    }

}
