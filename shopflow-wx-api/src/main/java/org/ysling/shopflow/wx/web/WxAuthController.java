package org.ysling.shopflow.wx.web;
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
import org.redisson.api.RateIntervalUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.annotation.JsonBody;
import org.ysling.shopflow.core.redis.annotation.RequestRateLimiter;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.ysling.shopflow.db.entity.UserInfo;
import org.ysling.shopflow.wx.model.auth.body.WxLoginInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.ysling.shopflow.wx.model.auth.body.*;
import org.ysling.shopflow.wx.web.impl.WxWebAuthService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 鉴权服务
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/auth")
@Validated
public class WxAuthController {

    @Autowired
    private WxWebAuthService authService;


    /**
     * 扫码授权登陆
     */
    @PostMapping("login_by_qr")
    public Object loginByQr(@LoginUser String userId, @Valid @RequestBody AuthLoginByQrBody body) {
        return authService.loginByQr(userId , body);
    }

    /**
     * 账号登录
     */
    @PostMapping("login")
    public Object login(@LoginUser String userId, @Valid @RequestBody AuthLoginBody body) {
        return authService.login(userId , body);
    }

    /**
     * 旧 H5 普通用户账号登录
     */
    @PostMapping("login_legacy")
    public Object legacyLogin(@Valid @RequestBody AuthLegacyLoginBody body) {
        return authService.legacyLogin(body);
    }

    /**
     * 请求手机验证码
     * TODO
     * 这里需要一定机制防止短信验证码被滥用
     * @param mobile 手机号码
     */
    @PostMapping("captcha/mobile")
    @RequestRateLimiter(rate = 10, rateInterval = 1, timeUnit = RateIntervalUnit.DAYS , errMsg = "验证码申请超过单日限制")
    public Object mobileCaptcha(@LoginUser(require = false) String userId, @JsonBody String mobile) {
        return authService.mobileCaptcha(userId , mobile);
    }


    /**
     * 请求邮箱验证码
     * TODO
     * 这里需要一定机制防止短信验证码被滥用
     * @param username 邮箱 { username }
     */
    @PostMapping("captcha/mail")
    @RequestRateLimiter(rate = 10, rateInterval = 1, timeUnit = RateIntervalUnit.DAYS , errMsg = "验证码申请超过单日限制")
    public Object mailCaptcha(@JsonBody String username) {
        return authService.mailCaptcha(username);
    }

    /**
     * 微信登录
     */
    @PostMapping("login_by_weixin")
    public Object loginByWeixin(@Valid @RequestBody WxLoginInfo wxLoginInfo, HttpServletRequest request) {
        return authService.loginByWeixin(wxLoginInfo , request);
    }

    /**
     * 账号注册
     * @param body    请求内容
     * @param request @Ignore
     * @return 登录结果
     */
    @PostMapping("register")
    public Object register(@Valid @RequestBody AuthRegisterBody body, HttpServletRequest request){
        return authService.register(body , request);
    }

    /**
     * 账号密码重置
     * @param body    请求内容
     * @return 登录结果
     */
    @PostMapping("reset")
    public Object reset(@Valid @RequestBody AuthResetBody body){
        return authService.reset(body);
    }

    /**
     * 账号手机号码重置
     * @param userId  @Ignore
     * @param body   请求内容
     */
    @PostMapping("resetPhone")
    public Object resetPhone(@LoginUser String userId, @Valid @RequestBody AuthResetBody body){
        return authService.resetPhone(userId , body);
    }

    /**
     * 账号信息更新
     * @param userId  @Ignore
     * @param body    请求内容
     */
    @PostMapping("profile")
    public Object profile(@LoginUser String userId, @Valid @RequestBody UserInfo body , HttpServletRequest request){
        return authService.profile(userId , body , request);
    }

    /**
     * 微信手机号码绑定
     * @param userId  @Ignore
     * @param body    请求内容
     */
    @PostMapping("bindPhone")
    public Object bindPhone(@LoginUser String userId, @Valid @RequestBody AuthBindPhoneBody body){
        return authService.bindPhone(userId , body);
    }


    /**
     * 退出登陆
     */
    @PostMapping("logout")
    public Object logout() {
        return authService.logout();
    }


}
