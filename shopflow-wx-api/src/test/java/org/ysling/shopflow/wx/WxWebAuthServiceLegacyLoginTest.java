package org.ysling.shopflow.wx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.core.utils.token.TokenManager;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.enums.UserStatus;
import org.ysling.shopflow.wx.model.auth.body.AuthLegacyLoginBody;
import org.ysling.shopflow.wx.model.auth.result.WxLoginResult;
import org.ysling.shopflow.wx.service.WxUserService;
import org.ysling.shopflow.wx.web.impl.WxWebAuthService;

import java.util.Collections;

public class WxWebAuthServiceLegacyLoginTest {

    private WxWebAuthService authService;
    private WxUserService userService;

    @Before
    public void setUp() {
        authService = new WxWebAuthService();
        userService = Mockito.mock(WxUserService.class);
        ReflectionTestUtils.setField(authService, "userService", userService);
    }

    @Test
    public void shouldRejectLegacyEmailLoginClearly() {
        AuthLegacyLoginBody body = new AuthLegacyLoginBody();
        body.setEmail("user123@example.com");
        body.setPassword("secret123");

        ResponseUtil<?> response = (ResponseUtil<?>) authService.legacyLogin(body);

        Assert.assertEquals("failed", response.getErrno());
        Assert.assertEquals("当前暂不支持邮箱登录", response.getErrmsg());
    }

    @Test
    public void shouldLoginLegacyUserByMobile() {
        AuthLegacyLoginBody body = new AuthLegacyLoginBody();
        body.setMobile("17779897985");
        body.setPassword("secret123");

        ShopflowUser user = new ShopflowUser();
        user.setId("1673184912431255552");
        user.setMobile("17779897985");
        user.setNickName("测试昵称");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setPassword("secret123");
        user.setStatus(UserStatus.STATUS_NORMAL.getStatus());

        Mockito.when(userService.queryByMobile("17779897985"))
                .thenReturn(Collections.singletonList(user));

        ResponseUtil<?> response = (ResponseUtil<?>) authService.legacyLogin(body);

        Assert.assertEquals("success", response.getErrno());
        Assert.assertTrue(response.getData() instanceof WxLoginResult);
        WxLoginResult result = (WxLoginResult) response.getData();
        Assert.assertEquals("1673184912431255552", TokenManager.getUserId(result.getUserToken()));
        Assert.assertEquals("测试昵称", result.getUserInfo().getNickName());
    }

    @Test
    public void shouldLoginLegacyUserByUsernameWithBcryptPassword() {
        AuthLegacyLoginBody body = new AuthLegacyLoginBody();
        body.setUsername("user123");
        body.setPassword("secret123");

        ShopflowUser user = new ShopflowUser();
        user.setId("1673184912431255552");
        user.setUsername("user123");
        user.setNickName("测试昵称");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setPassword(CryptoUtil.encode("secret123"));
        user.setStatus(UserStatus.STATUS_NORMAL.getStatus());

        Mockito.when(userService.queryByUsername("user123"))
                .thenReturn(Collections.singletonList(user));

        ResponseUtil<?> response = (ResponseUtil<?>) authService.legacyLogin(body);

        Assert.assertEquals("success", response.getErrno());
        Assert.assertTrue(response.getData() instanceof WxLoginResult);
    }

    @Test
    public void shouldValidateAnonymousMobileCaptchaInsteadOfRequiringLogin() {
        ResponseUtil<?> response = (ResponseUtil<?>) authService.mobileCaptcha(null, "bad-mobile");

        Assert.assertEquals("A0410", response.getErrno());
    }
}
