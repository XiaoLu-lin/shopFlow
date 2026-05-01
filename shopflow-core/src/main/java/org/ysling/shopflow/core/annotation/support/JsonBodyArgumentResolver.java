package org.ysling.shopflow.core.annotation.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.utils.JacksonUtil;
import org.ysling.shopflow.core.utils.http.GlobalWebUtil;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义请求参数注解
 * @author Ysling
 */
@Slf4j
public class JsonBodyArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonBody.class);
    }

    @Override
    public Object resolveArgument(@Nonnull MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  @Nonnull NativeWebRequest request,
                                  WebDataBinderFactory factory) {

        //获取自定义注解
        JsonBody jsonBody = parameter.getParameterAnnotation(JsonBody.class);
        if (jsonBody == null){
            return null;
        }
        //请求参数json
        String body = this.getJsonObject(request);
        //参数类型
        Class<?> classType = parameter.getParameterType();
        //获取注解参数名称
        String paramName = this.getParamName(parameter, jsonBody);
        //获取参数值
        Object paramValue = JacksonUtil.parseObject(body, paramName, classType);
        //判断参数是否必须
        if (paramValue == null && jsonBody.require()) {
            throw new RuntimeException("请求参数[" + paramName + "]不能为空。");
        }
        return paramValue;
    }


    /**
     * 获取参数名称
     */
    private String getParamName(MethodParameter parameter, JsonBody jsonBody) {
        String value = jsonBody.value();
        if (!StringUtils.hasText(value)) {
            value = parameter.getParameterName();
        }
        return value;
    }

    /**
     * 获取请求参数并转为json
     */
    private String getJsonObject(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(request != null, "No HttpServletRequest");
        return GlobalWebUtil.getJsonBody(request);
    }

}
