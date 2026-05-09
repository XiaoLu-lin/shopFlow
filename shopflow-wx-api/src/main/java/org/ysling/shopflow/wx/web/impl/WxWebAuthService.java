package org.ysling.shopflow.wx.web.impl;
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

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.notify.model.NotifyType;
import org.ysling.shopflow.core.notify.service.NotifyMailService;
import org.ysling.shopflow.core.notify.service.NotifyMobileService;
import org.ysling.shopflow.core.satoken.handler.AuthenticationInfo;
import org.ysling.shopflow.core.service.CouponAssignService;
import org.ysling.shopflow.core.utils.RandomStrUtil;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.captcha.CaptchaManager;
import org.ysling.shopflow.core.utils.ip.IpUtil;
import org.ysling.shopflow.core.utils.response.ResponseStatus;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.utils.token.TokenManager;
import org.ysling.shopflow.db.domain.ShopflowAdmin;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.AdminInfo;
import org.ysling.shopflow.db.entity.UserInfo;
import org.ysling.shopflow.db.enums.UserGender;
import org.ysling.shopflow.db.enums.UserLevel;
import org.ysling.shopflow.db.enums.UserStatus;
import org.ysling.shopflow.wx.model.auth.body.*;
import org.ysling.shopflow.wx.model.auth.result.LoginQrResult;
import org.ysling.shopflow.wx.model.auth.result.LoginResult;
import org.ysling.shopflow.wx.model.auth.result.WxAuthResult;
import org.ysling.shopflow.wx.model.auth.result.WxLoginResult;
import org.ysling.shopflow.wx.service.WxAdminService;
import org.ysling.shopflow.wx.service.WxAuthService;
import org.ysling.shopflow.wx.service.WxUserService;
import org.ysling.shopflow.wx.support.LegacyH5AuthSupport;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 鉴权服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxWebAuthService {

    @Autowired
    private WxUserService userService;
    @Autowired
    private WxAdminService adminService;
    @Autowired
    private NotifyMobileService mobileService;
    @Autowired
    private NotifyMailService mailService;
    @Autowired
    private CouponAssignService couponAssignService;
    @Autowired
    private WxAuthService wxAuthService;


    /**
     * 扫码授权登陆
     */
    public Object loginByQr(String userId, AuthLoginByQrBody body) {
        ShopflowUser user = userService.findById(userId);
        if (user == null){
            return ResponseUtil.fail("授权失败1");
        }
        ShopflowAdmin admin = adminService.findByOpenId(user.getOpenid());
        if (admin == null){
            return ResponseUtil.fail("授权失败2");
        }

        //授权登录
        AuthenticationInfo.loginQr(admin);

        // adminInfo
        AdminInfo adminInfo = new AdminInfo();
        BeanUtil.copyProperties(admin, adminInfo);
        // 登陆信息
        LoginQrResult result = new LoginQrResult();
        result.setAdminInfo(adminInfo);
        result.setToken(StpUtil.getTokenValue());
        result.setTenantId(TokenManager.createTenantToken(admin.getTenantId()));
        //发送socket
        wxAuthService.sendSocket(body.getAuthCode(), result);
        return ResponseUtil.ok();
    }

    /**
     * 账号登录
     */
    public Object login(String userId, AuthLoginBody body) {
        String username = body.getUsername();
        String password = body.getPassword();
        //创建账户实体并登陆
        ShopflowAdmin admin = new ShopflowAdmin();
        admin.setUsername(username);
        admin.setPassword(password);
        AuthenticationInfo.login(admin, body.getCode());
        //登陆成功
        ShopflowUser user = userService.findById(userId);
        if (StringUtils.hasText(admin.getOpenid())){
            if (!admin.getOpenid().equals(user.getOpenid())){
                throw new RuntimeException("该管理员账户已绑定其他用户");
            }
        } else {
            ShopflowAdmin service = adminService.findById(admin.getId());
            service.setOpenid(user.getOpenid());
            if (adminService.updateVersionSelective(service) == 0){
                throw new RuntimeException("网络繁忙,请重试");
            }
        }
        //绑定新的管理员账户后，解除原有账户的绑定
        ShopflowAdmin originalAdmin = adminService.findByOpenId(user.getOpenid());
        if (originalAdmin != null && !originalAdmin.getId().equals(admin.getId())){
            originalAdmin.setOpenid("");
            if (adminService.updateVersionSelective(originalAdmin) == 0){
                throw new RuntimeException("网络繁忙,请重试");
            }
        }

        // adminInfo
        AdminInfo adminInfo = new AdminInfo();
        BeanUtil.copyProperties(admin , adminInfo);
        LoginResult result = new LoginResult();
        result.setAdminInfo(adminInfo);
        result.setAdminToken(StpUtil.getTokenValue());
        return ResponseUtil.ok(result);
    }

    /**
     * 旧 H5 普通用户账号登录
     */
    public Object legacyLogin(AuthLegacyLoginBody body) {
        String mobile = LegacyH5AuthSupport.normalize(body.getMobile());
        String username = LegacyH5AuthSupport.normalize(body.getUsername());
        String email = LegacyH5AuthSupport.normalize(body.getEmail());
        String password = LegacyH5AuthSupport.normalize(body.getPassword());

        if (StringUtils.hasText(email)) {
            return ResponseUtil.fail("当前暂不支持邮箱登录");
        }

        List<ShopflowUser> userList = Collections.emptyList();
        if (StringUtils.hasText(mobile)) {
            userList = userService.queryByMobile(mobile);
        } else if (StringUtils.hasText(username)) {
            userList = userService.queryByUsername(username);
        }

        if (userList.size() != 1) {
            return ResponseUtil.fail("账号或密码错误");
        }

        ShopflowUser user = userList.get(0);
        if (user == null) {
            return ResponseUtil.fail("账号或密码错误");
        }
        if (user.getStatus().equals(UserStatus.STATUS_DISABLED.getStatus())){
            return ResponseUtil.fail("账号被禁用");
        }
        if (user.getStatus().equals(UserStatus.STATUS_OUT.getStatus())){
            return ResponseUtil.fail("账号已注销");
        }
        if (!LegacyH5AuthSupport.matchesPassword(password, user.getPassword())) {
            return ResponseUtil.fail("账号或密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        if (adminService != null) {
            // no-op branch keeps existing bean graph untouched; user login is stateless
        }
        return ResponseUtil.ok(LegacyH5AuthSupport.buildLoginResult(user));
    }

    /**
     * 请求手机验证码
     * TODO
     * 这里需要一定机制防止短信验证码被滥用
     * @param mobile 手机号码
     */
    public Object mobileCaptcha(String userId, String mobile) {
        if (!RegexUtil.isMobileSimple(mobile)) {
            return ResponseUtil.badArgumentValue();
        }

        String code = RandomStrUtil.getRandom(6, RandomStrUtil.TYPE.NUMBER);;
        if (CaptchaManager.addToCache(mobile, code)) {
            return ResponseUtil.fail("验证码未超时，不能发送");
        }

        //发送验证码到手机号
        if (mobileService.isSmsEnable()){
            mobileService.notifySmsTemplate(mobile, NotifyType.CAPTCHA, new String[]{code});
            return ResponseUtil.ok("验证码发送成功，请注意查收");
        }
        return ResponseUtil.ok("验证码发送成功，请注意查收" + "登录验证码："+code);
    }


    /**
     * 请求邮箱验证码
     * TODO
     * 这里需要一定机制防止短信验证码被滥用
     * @param username 邮箱 { username }
     */
    public Object mailCaptcha(String username) {
        List<ShopflowAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() != 1){
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0110);
        }

        String code = RandomStrUtil.getRandom(6, RandomStrUtil.TYPE.NUMBER);
        if (CaptchaManager.addToCache(username, code)) {
            return ResponseUtil.fail(ResponseStatus.USER_ERROR_A0242);
        }

        ShopflowAdmin admin = adminList.get(0);
        if (mailService.isMailEnable() && !RegexUtil.isQQMail(admin.getMail())){
            mailService.notifyMail("登录验证码："+code, "验证码30分钟内有效，如发送错误请您忽略！", admin.getMail());
            return ResponseUtil.ok("验证码发送成功，请注意查收");
        }
        return ResponseUtil.ok("验证码发送成功，请注意查收" + "登录验证码："+code);
    }

    /**
     * 微信登录
     */
    public Object loginByWeixin(WxLoginInfo wxLoginInfo, HttpServletRequest request) {
        String wxCode = wxLoginInfo.getWxCode();
        String phoneCode = wxLoginInfo.getPhoneCode();
        String inviter = wxLoginInfo.getInviter();
        UserInfo userInfo = wxLoginInfo.getUserInfo();
        //获取用户授权信息
        WxAuthResult wxAuth = wxAuthService.wxAuth(wxCode);
        String openId = wxAuth.getOpenId();
        String sessionKey = wxAuth.getSessionKey();
        String mobile = wxAuthService.getPhoneNumber(phoneCode);
        userInfo.setOpenId(openId);
        //获取用户信息
        ShopflowUser user = userService.queryByOid(openId);
        if (user == null) {
            user = new ShopflowUser();
            user.setUsername(mobile);
            user.setPassword(mobile);
            user.setOpenid(openId);
            user.setMobile(mobile);
            user.setAvatarUrl(userInfo.getAvatarUrl());
            user.setNickName(userInfo.getNickName());
            user.setGender(userInfo.getGender());
            user.setUserLevel(UserLevel.USER_LEVEL_0.getStatus());
            user.setStatus(UserStatus.STATUS_NORMAL.getStatus());
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            user.setSessionKey(sessionKey);
            userService.add(user);
            // 新用户发送注册优惠券
            couponAssignService.assignForRegister(user.getId());
        } else {
            if (user.getStatus().equals(UserStatus.STATUS_DISABLED.getStatus())){
                return ResponseUtil.fail("账号被禁用");
            }
            if (user.getStatus().equals(UserStatus.STATUS_OUT.getStatus())){
                return ResponseUtil.fail("账号已注销");
            }
            if (StringUtils.hasText(inviter) && user.getInviter() == null) {
                user.setInviter(inviter);
            }
            user.setOpenid(openId);
            user.setSessionKey(sessionKey);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(IpUtil.getIpAddr(request));
            if (userService.updateVersionSelective(user) == 0) {
                return ResponseUtil.updatedDataFailed();
            }
        }

        if (StringUtils.hasText(inviter)){
            ShopflowUser inviterUser = userService.findById(inviter);
            if (inviterUser != null){
                Integer count = userService.countUser(inviter);
                inviterUser.setStatus(UserLevel.parseCount(count));
                if (userService.updateVersionSelective(inviterUser) == 0){
                    throw new RuntimeException("用户信息更新失败，请重试");
                }
            }
        }

        BeanUtil.copyProperties(user , userInfo);
        // token
        String token = TokenManager.createUserToken(user.getId());
        WxLoginResult result = new WxLoginResult();
        result.setUserToken(token);
        result.setUserInfo(userInfo);
        return ResponseUtil.ok(result);
    }

    /**
     * 账号注册
     * @param body    请求内容
     * @param request @Ignore
     * @return 登录结果
     */
    public Object register(AuthRegisterBody body, HttpServletRequest request){
        String username = LegacyH5AuthSupport.normalize(body.getUsername());
        String password = body.getPassword();
        String mobile = body.getMobile();
        String code = body.getCode();
        String wxCode = body.getWxCode();

        if (StringUtils.hasText(username)) {
            List<ShopflowUser> usernameList = userService.queryByUsername(username);
            if (usernameList.size() > 0) {
                return ResponseUtil.fail("用户名已注册");
            }
        }

        List<ShopflowUser> userList = userService.queryByMobile(mobile);
        if (userList.size() > 0) {
            return ResponseUtil.fail( "手机号已注册");
        }
        if (!RegexUtil.isMobileSimple(mobile)) {
            return ResponseUtil.fail( "手机号格式不正确");
        }

        //判断验证码是否正确
        if (CaptchaManager.isCachedCaptcha(mobile,code)) {
            return ResponseUtil.fail( "验证码错误");
        }

        String openId = "";
        // 非空，则是小程序注册
        // 继续验证openid
        if(!Objects.isNull(wxCode)) {
            WxAuthResult wxAuth = wxAuthService.wxAuth(wxCode);
            if (wxAuth == null){
                return ResponseUtil.fail( "授权信息获取失败");
            }
            openId = wxAuth.getOpenId();
            userList = userService.queryByOpenid(openId);
            if (userList.size() > 1) {
                return ResponseUtil.serious();
            }
            if (userList.size() == 1) {
                ShopflowUser checkUser = userList.get(0);
                String checkPassword = checkUser.getPassword();
                if (!checkPassword.equals(openId)) {
                    return ResponseUtil.fail( "openid已绑定账号");
                }
            }
        }

        ShopflowUser user = new ShopflowUser();
        user.setUsername(StringUtils.hasText(username) ? username : mobile);
        user.setPassword(CryptoUtil.encode(password));
        user.setMobile(mobile);
        user.setOpenid(openId);
        user.setAvatarUrl("https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64");
        user.setNickName(mobile);
        user.setGender(UserGender.USER_GENDER_0.getStatus());
        user.setUserLevel(UserLevel.USER_LEVEL_0.getStatus());
        user.setStatus(UserStatus.STATUS_NORMAL.getStatus());
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        userService.add(user);

        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(StringUtils.hasText(username) ? username : mobile);
        userInfo.setAvatarUrl(user.getAvatarUrl());

        // token
        String token = TokenManager.createUserToken(user.getId());
        WxLoginResult result = new WxLoginResult();
        result.setUserToken(token);
        result.setUserInfo(userInfo);
        return ResponseUtil.ok(result);
    }

    /**
     * 账号密码重置
     * @param body    请求内容
     * @return 登录结果
     */
    public Object reset(AuthResetBody body){
        String code = body.getCode();
        String mobile = body.getMobile();
        String password = body.getPassword();

        //判断验证码是否正确
        if (CaptchaManager.isCachedCaptcha(mobile,code)) {
            return ResponseUtil.fail( "验证码错误");
        }

        List<ShopflowUser> userList = userService.queryByMobile(mobile);
        if (userList.size() > 1) {
            return ResponseUtil.serious();
        } else if (userList.size() == 0) {
            return ResponseUtil.fail( "手机号未注册");
        }

        ShopflowUser user = userList.get(0);
        user.setPassword(CryptoUtil.encode(password));
        if (userService.updateVersionSelective(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 账号手机号码重置
     * @param userId  @Ignore
     * @param body   请求内容
     */
    public Object resetPhone(String userId, AuthResetBody body){
        String code = body.getCode();
        String mobile = body.getMobile();
        String password = body.getPassword();

        //判断验证码是否正确
        if (CaptchaManager.isCachedCaptcha(mobile,code)) {
            return ResponseUtil.fail( "验证码错误");
        }

        List<ShopflowUser> userList = userService.queryByMobile(mobile);
        if (userList.size() > 1) {
            return ResponseUtil.fail( "手机号已注册");
        }

        ShopflowUser user = userService.findById(userId);
        if (!CryptoUtil.matches(password, user.getPassword())) {
            return ResponseUtil.fail( "账号密码不对");
        }

        user.setMobile(mobile);
        if (userService.updateVersionSelective(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 账号信息更新
     * @param userId  @Ignore
     * @param body    请求内容
     */
    public Object profile(String userId, UserInfo body , HttpServletRequest request){
        ShopflowUser user = userService.findById(userId);
        BeanUtil.copyProperties(body, user);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        if (userService.updateVersionSelective(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }

    /**
     * 微信手机号码绑定
     * @param userId  @Ignore
     * @param body    请求内容
     */
    public Object bindPhone(String userId, AuthBindPhoneBody body){
        String iv = body.getIv();
        String encryptedData = body.getEncryptedData();
        ShopflowUser user = userService.findById(userId);
        user.setMobile(wxAuthService.getPhoneNumber(user.getSessionKey(),encryptedData,iv));
        if (userService.updateVersionSelective(user) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok();
    }


    /**
     * 退出登陆
     */
    public Object logout() {
        return ResponseUtil.unlogin();
    }


}
