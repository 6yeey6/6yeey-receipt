package com.ibg.receipt.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA工具类，来自于山东信托DEMO
 *
 * @author renxin
 */
@Slf4j
public class RsaUtils {

    /** MD5withRSA 签名方式 */
    private static final String MD5_WITH_RSA = "MD5withRSA";
    /** RSA */
    private static final String RSA = "RSA";
    private static final String PROVIDER = "BC";

    /**
     * MD5_WITH_RSA数字签名验证
     *
     * @param content      验签内容
     * @param sign         签名
     * @param publicKeyStr 公钥字符串
     * @return 验证结果
     */
    public static boolean verify(String content, String sign, String publicKeyStr) {
        try {
            if (StringUtils.isEmpty(content)) {
                log.warn("验签失败，content为空");
                return false;
            }
            if (StringUtils.isEmpty(sign)) {
                log.warn("验签失败，sign签名为空");
                return false;
            }
            if (StringUtils.isEmpty(publicKeyStr)) {
                log.warn("验签失败，公钥为空");
                return false;
            }
            PublicKey publicKey = getPublicKey(publicKeyStr);
            Signature signature = Signature.getInstance(MD5_WITH_RSA);
            signature.initVerify(publicKey);
            signature.update(content.getBytes());
            return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
        } catch (Exception e) {
            log.error("验签失败", e);
            return false;
        }
    }


    /**
     * RSA签名
     *
     * @param content       待签名内容
     * @param privateKeyStr 私钥字符串
     * @return RSA签名字符串
     */
    public static String sign(String content, String privateKeyStr) {
        try {
            if (StringUtils.isEmpty(content)) {
                log.warn("签名失败，content为空");
                return null;
            }
            PrivateKey privateKey = getPrivateKeyFromPKCS8(privateKeyStr);
            Signature signature = Signature.getInstance(MD5_WITH_RSA);
            signature.initSign(privateKey);
            signature.update(content.getBytes());
            byte[] signed = signature.sign();
            return new String(Base64.getEncoder().encode(signed));
        } catch (Exception e) {
            log.error("签名失败", e);
            return null;
        }
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥
     * @return 公钥类
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        if (StringUtils.isEmpty(publicKey)) {
            log.warn("公钥为空");
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        byte[] encodedKey = Base64.getDecoder().decode(publicKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * 获取PKCS8格式私钥
     *
     * @param privateKey 私钥
     * @return 私钥类
     */
    private static PrivateKey getPrivateKeyFromPKCS8(String privateKey) throws Exception {
        byte[] encodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        return KeyFactory.getInstance(RSA).generatePrivate(keySpec);
    }


    /**
     * RSA公钥加密
     *
     * @param content  明文
     * @param publicKeyStr  公钥
     * @return
     */
    public static String encrypt(String content, String publicKeyStr) {
        try {
            if (StringUtils.isEmpty(content)) {
                log.warn("content为空");
                return null;
            }
            PublicKey publicKey = getPublicKey(publicKeyStr);
            if (publicKey == null) {
                log.warn("公钥为空");
                return null;
            }
            // 最大加密长度
            int maxEncryptSize = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            byte[] contentBytes = content.getBytes();

            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(RSA, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] bytes = doFinalByMaxSize(cipher, contentBytes, maxEncryptSize);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("exception message is:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 分段加解密
     */
    private static byte[] doFinalByMaxSize(Cipher cipher, byte[] contentBytes, int maxByteSize) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int inputLength = contentBytes.length;
            int offSet = 0;
            byte[] tmp;
            while (inputLength - offSet > 0) {
                if (inputLength - offSet > maxByteSize) {
                    tmp = cipher.doFinal(contentBytes, offSet, maxByteSize);
                } else {
                    tmp = cipher.doFinal(contentBytes, offSet, inputLength - offSet);
                }
                outputStream.write(tmp, 0, tmp.length);
                offSet += maxByteSize;
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * RSA私钥解密
     * @param content   密文
     * @param privateKeyStr  rsa私钥
     * @return
     */
    public static String decrypt(String content, String privateKeyStr) {
        try {
            if (StringUtils.isEmpty(content)) {
                log.warn("content为空");
                return null;
            }
            PrivateKey privateKey = getPrivateKeyFromPKCS8(privateKeyStr);
            if (privateKey == null) {
                log.warn("私钥为空");
                return null;
            }
            int maxDecryptSize = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
            byte[] contentBytes = Base64.getDecoder().decode(content);

            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(RSA, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] original = doFinalByMaxSize(cipher, contentBytes, maxDecryptSize);
            return new String(original);
        } catch (Exception e) {
            log.error("exception message is:{}", e.getMessage());
            return null;
        }
    }

}
