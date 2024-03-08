package com.ibg.receipt.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Arrays;

@Slf4j
public class AesUtils {

    public static byte[] encrypt(String content, String password) {
        try {
            byte[] enCodeFormat = password.getBytes();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (Exception e) {
            log.error(" aes encrypt error: [" + password + "]", e);
            return null;
        }
    }

    public static byte[] decrypt(byte[] content, String password) {
        try {
            byte[] enCodeFormat = password.getBytes();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            log.error(" aes decrypt error: [" + password + "]", e);
            return null;
        }
    }

    public static String encryptToBase64(String content, String password) {
        try {
            byte[] encryptBytes = encrypt(content, password);
            String s = Base64.encodeBase64String(encryptBytes);
            return s;
        } catch (Exception e) {
            log.error(" aes decrypt error: [" + password + "]", e);
            return content;
        }
    }

    public static String decryptFromBase64(String content, String password) {
        try {
            byte[] decryptBytes = Base64.decodeBase64(content);
            byte[] s = decrypt(decryptBytes, password);
            return new String(s);
        } catch (Exception e) {
            log.error(" aes decrypt error: [" + password + "]", e);
            return content;
        }
    }

    // AES解密（thread提供）
    public static String aesDecrypt(String passwordhex, String strKey) {
        try {
            byte[] keyBytes = Arrays.copyOf(strKey.getBytes("ASCII"), 16);

            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher decipher = Cipher.getInstance("AES");

            decipher.init(Cipher.DECRYPT_MODE, key);

            char[] cleartext = passwordhex.toCharArray();

            byte[] decodeHex = Hex.decodeHex(cleartext);

            byte[] ciphertextBytes = decipher.doFinal(decodeHex);

            return new String(ciphertextBytes);

        } catch (Exception e) {
            log.error("decode error", e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String content = FileUtils.readFileToString(new File("f:/elftest/online/1.txt"), "GBK");
        String password = "PAY_DAY_LOAN_PWD";
        String s = encryptToBase64(content, password);
        String t = decryptFromBase64(s, password);
        System.out.println(s);
        System.out.println(t);
    }
}
