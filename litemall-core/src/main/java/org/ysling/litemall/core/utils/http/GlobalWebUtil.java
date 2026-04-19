package org.ysling.litemall.core.utils.http;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局请求工具类
 */
public class GlobalWebUtil {

    /**
     * 获取HttpServletRequest对象
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
        if (requestAttributes != null) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return request;
    }

    /**
     * 获取HttpServletResponse对象
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        HttpServletResponse response = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
        if (requestAttributes != null) {
            response = ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        return response;
    }


    /**
     * 发送响应内容
     * 这里返回固定的BaseResponse对象给前端，直接抛异常时发现，在filter中的异常，全局异常处理类无法处理
     * @param response 响应请求
     * @param message   响应内容
     * @throws IOException 异常
     */
    public static void sendMessage(ServletResponse response , String message) throws IOException {
        //这个要在PrintWriter创建之前使用才生效
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        //这里返回固定的BaseResponse对象给前端，直接抛异常时发现，在filter中的异常，GlobalExceptionHandler全局异常处理类无法处理
        PrintWriter out = response.getWriter();
        out.print(JSONObject.toJSONString(ResponseUtil.fail(message), SerializerFeature.WriteNullStringAsEmpty));
        out.flush();
        out.close();
    }

    /**
     * 获取请求参数并转为json
     */
    public static String getJsonBody(HttpServletRequest request) {
        Assert.state(request != null, "No HttpServletRequest");
        StringBuilder value = new StringBuilder();
        //获取表单请求参数
        Map<String , String[]> params = request.getParameterMap();
        if (params.size() > 0){
            Map<Object , Object> tmp = new HashMap<>();
            for (Map.Entry<String , String[]> param : params.entrySet()) {
                if(param.getValue().length == 1){
                    tmp.put(param.getKey(), param.getValue()[0]);
                }else{
                    tmp.put(param.getKey(), param.getValue());
                }
            }
            value.append(JacksonUtil.toJson(tmp));
        }else {
            try {
                //获取请求体参数
                return getPostBody(request);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return value.toString();
    }

    /**
     * 获取请求替参数
     * @param request 请求
     * @return 参数
     */
    public static String getPostBody(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
            if (request instanceof CustomRequestWrapper){
                return new String(((CustomRequestWrapper) request).getBody());
            }else {
                sb = new StringBuilder();
                br = request.getReader();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //转字符串并删除空格
        return sb.toString().replaceAll("\\s", "");
    }

}

