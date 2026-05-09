package org.ysling.shopflow.wx;

import org.junit.Assert;
import org.junit.Test;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.token.TokenManager;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.UserInfo;
import org.ysling.shopflow.db.enums.UserStatus;
import org.ysling.shopflow.wx.model.auth.result.WxLoginResult;
import org.ysling.shopflow.wx.support.LegacyH5AuthSupport;

public class LegacyH5AuthSupportTest {

    @Test
    public void shouldResolveMobileAccountType() {
        Assert.assertEquals(
                LegacyH5AuthSupport.AccountType.MOBILE,
                LegacyH5AuthSupport.resolveAccountType("17779897985")
        );
    }

    @Test
    public void shouldResolveUsernameAccountType() {
        Assert.assertEquals(
                LegacyH5AuthSupport.AccountType.USERNAME,
                LegacyH5AuthSupport.resolveAccountType("user123")
        );
    }

    @Test
    public void shouldResolveEmailAccountType() {
        Assert.assertEquals(
                LegacyH5AuthSupport.AccountType.EMAIL,
                LegacyH5AuthSupport.resolveAccountType("user123@example.com")
        );
    }

    @Test
    public void shouldMatchBcryptPassword() {
        String encoded = CryptoUtil.encode("secret123");
        Assert.assertTrue(LegacyH5AuthSupport.matchesPassword("secret123", encoded));
    }

    @Test
    public void shouldMatchLegacyPlaintextPassword() {
        Assert.assertTrue(LegacyH5AuthSupport.matchesPassword("secret123", "secret123"));
    }

    @Test
    public void shouldBuildLegacyLoginResult() {
        ShopflowUser user = new ShopflowUser();
        user.setId("1673184912431255552");
        user.setNickName("测试用户");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setMobile("17779897985");
        user.setStatus(UserStatus.STATUS_NORMAL.getStatus());

        WxLoginResult result = LegacyH5AuthSupport.buildLoginResult(user);
        UserInfo userInfo = result.getUserInfo();

        Assert.assertEquals("1673184912431255552", TokenManager.getUserId(result.getUserToken()));
        Assert.assertNotNull(userInfo);
        Assert.assertEquals("测试用户", userInfo.getNickName());
        Assert.assertEquals("https://example.com/avatar.png", userInfo.getAvatarUrl());
        Assert.assertEquals("17779897985", userInfo.getMobile());
    }
}
