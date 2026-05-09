package org.ysling.shopflow.wx.support;

import org.springframework.util.StringUtils;
import org.ysling.shopflow.core.utils.RegexUtil;
import org.ysling.shopflow.core.utils.bcrypt.CryptoUtil;
import org.ysling.shopflow.core.utils.token.TokenManager;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.UserInfo;
import org.ysling.shopflow.wx.model.auth.result.WxLoginResult;

/**
 * 旧 H5 鉴权兼容工具
 * @author Ysling
 */
public class LegacyH5AuthSupport {

    private LegacyH5AuthSupport() {
    }

    /**
     * 账号类型
     */
    public enum AccountType {
        USERNAME,
        MOBILE,
        EMAIL
    }

    /**
     * 识别旧 H5 账号类型
     */
    public static AccountType resolveAccountType(String account) {
        String value = normalize(account);
        if (!StringUtils.hasText(value)) {
            return AccountType.USERNAME;
        }
        if (RegexUtil.isMobileSimple(value)) {
            return AccountType.MOBILE;
        }
        if (RegexUtil.isEmail(value)) {
            return AccountType.EMAIL;
        }
        return AccountType.USERNAME;
    }

    /**
     * 同时兼容历史明文密码和当前 BCrypt 密码
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        String raw = normalize(rawPassword);
        if (!StringUtils.hasText(raw) || !StringUtils.hasText(encodedPassword)) {
            return false;
        }
        if (isBcryptValue(encodedPassword)) {
            return CryptoUtil.matches(raw, encodedPassword);
        }
        return raw.equals(encodedPassword);
    }

    /**
     * 构造用户登录返回
     */
    public static WxLoginResult buildLoginResult(ShopflowUser user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setOpenId(user.getOpenid());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setGender(user.getGender());
        userInfo.setMobile(user.getMobile());
        userInfo.setNickName(user.getNickName());
        userInfo.setShareUrl(user.getShareUrl());
        userInfo.setTrueName(user.getTrueName());
        WxLoginResult result = new WxLoginResult();
        result.setUserInfo(userInfo);
        result.setUserToken(TokenManager.createUserToken(user.getId()));
        return result;
    }

    /**
     * 去除首尾空白
     */
    public static String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private static boolean isBcryptValue(String password) {
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}
