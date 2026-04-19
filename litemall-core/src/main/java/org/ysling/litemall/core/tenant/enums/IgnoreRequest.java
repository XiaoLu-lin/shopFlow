package org.ysling.litemall.core.tenant.enums;

import org.springframework.util.AntPathMatcher;

/**
 * 多租户请求白名单枚举类
 * @author Ysling
 */
public enum IgnoreRequest {

    /**多租户请求白名单*/
    request11("/wx/home/auth", "小程序授权接口"),
    request10("/admin/storage/uploadFile", "富文本图片上传"),
    request9("/admin/auth/captcha", "登录验证码"),
    request8("/wx/msg/**", "微信消息推送"),
    request7("/admin/auth/login_by_qr", "扫码登录"),
    request6("/admin/auth/login", "后台登陆接口"),
    request4("/wx/storage/fetch/**", "图片查看接口"),
    request3("/wx/storage/download/**", "图片下载接口"),
    request2("/wx/order/pay-notify", "支付回调接口"),
    request1("/wx/home/index", "微信首页信息附带授权");

    public final String request;
    public final String message;

    IgnoreRequest(String request, String message) {
        this.request = request;
        this.message = message;
    }

    /**
     * 对比请求
     * @param request 请求地址
     * @return 存在true 不存在false
     */
    public static Boolean parseValue(String request) {
        if (request != null) {
            //地址匹配器
            AntPathMatcher matcher = new AntPathMatcher();
            for (IgnoreRequest item : values()) {
                if (matcher.match(item.request, request)) {
                    return true;
                }
            }
        }
        return false;
    }
}
