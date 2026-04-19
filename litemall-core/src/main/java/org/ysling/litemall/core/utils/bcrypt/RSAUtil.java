
package org.ysling.litemall.core.utils.bcrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {
	
	public final static String ALGORITHM = "RSA";
    public final static String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥密钥对
     */
    public static KeyPair getKey() throws Exception{
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        return generator.generateKeyPair();
    }
    
    private static Key getPublicKey(String key)throws Exception{
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
    
    private static Key getPrivateKey(String key)throws Exception{
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * 使用公钥进行加密
     */
    public static String encryptByPublicKey(String data,String key)throws Exception{
        Key k = getPublicKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(bytes);
    }
    
    /**
     * 使用私钥进行加密
     */
    public static String encryptByPrivateKey(String data,String key)throws Exception{
        Key k = getPrivateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(bytes);
    }
    
    /**
     * 使用密钥进行解密
     */
    public static String decryptByPrivateKey(String data,String key)throws Exception{
        Key k = getPrivateKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(Base64.decodeBase64(data));
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * 使用公钥进行解密
     */
    public static String decryptByPublicKey(String data,String key)throws Exception{
        Key k = getPublicKey(key);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] bytes = cipher.doFinal(Base64.decodeBase64(data));
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * 使用私钥进行签名
     */
    public static String sign(String data,String key)throws Exception{
        PrivateKey k = (PrivateKey)getPrivateKey(key);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(k);  
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(signature.sign());
    }
    
    /**
     * 使用公钥进行签名验证
     */
    public static boolean signVerify(String data,String key,String sign)throws Exception{
        PublicKey k = (PublicKey)getPublicKey(key);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(k);  
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.decodeBase64(sign));  
    }



    public static void main(String[] args) throws Exception{
        KeyPair keyPair = getKey();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
        RSAPublicKey  publicKey = (RSAPublicKey)keyPair.getPublic();
        
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        
        System.out.println("私钥：" + privateKeyStr);
        System.out.println("公钥：" + publicKeyStr);
        
        String data = "Hello,RSA,Hello,RSAHello,RSAHello,RSAHello,RSAHello,RSAHello,RSA";
        System.out.println("---------------公钥加密，私钥解密-----------------");
        String encryptedData = encryptByPublicKey(data,publicKeyStr);
        System.out.println("加密后：" + encryptedData);
        
        String decryptedData = decryptByPrivateKey(encryptedData, privateKeyStr);
        System.out.println("解密后：" + decryptedData);
        System.out.println("---------------私钥加密，公钥解密-----------------");
        
        encryptedData = encryptByPrivateKey(data,privateKeyStr);
        System.out.println("加密后：" + encryptedData);
        decryptedData = decryptByPublicKey(encryptedData, publicKeyStr);
        System.out.println("解密后：" + decryptedData);
        
        String sign = sign(data,privateKeyStr);
        System.out.println("签名：" + sign);
        System.out.println("签名验证：" + signVerify(data,publicKeyStr,sign));
        
        
    }

}
