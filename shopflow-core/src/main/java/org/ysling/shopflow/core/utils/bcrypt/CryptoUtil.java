package org.ysling.shopflow.core.utils.bcrypt;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.springframework.util.StringUtils;

/**
 * iUwaIDfcdto=
 * 加密解密
 * @author Ysling
 */
public class CryptoUtil {

    /**
     * 密钥，用于加密解密
     */
    private static final String SECRET_KEY = "iUwaIDfcdto=";

    /**
     * 生成密文
     * @param text  明文
     * @return 密文
     */
    public static String encrypt(String text) {
        return encrypt(text , SECRET_KEY);
    }

    /**
     * 生成密文
     * @param text  明文
     * @return 密文
     */
    public static String encrypt(String text, String secretKey) {
        return SaSecureUtil.aesEncrypt(secretKey, text);
    }

    /**
     * 密文解明文
     * @param encrypt 密文
     * @return 明文
     */
    public static String decode(String encrypt) {
        return decode(encrypt, SECRET_KEY);
    }

    /**
     * 密文解明文
     * @param encrypt 密文
     * @param secretKey 密钥
     * @return 明文
     */
    public static String decode(String encrypt, String secretKey) {
        return SaSecureUtil.aesDecrypt(secretKey, encrypt);
    }

    /**
     * 加密
     * @param rawPassword 原始密码
     * @return 加密后字符串
     */
    public static String encode(CharSequence rawPassword) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }

    /**
     * 检查被加密的字符串是否与原始字符串匹配
     * @param rawPassword       原始密码
     * @param encodedPassword   加密后密码
     * @return true
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return false;
        }
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }

}
