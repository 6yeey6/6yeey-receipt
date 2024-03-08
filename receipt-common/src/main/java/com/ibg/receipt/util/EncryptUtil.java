package com.ibg.receipt.util;

import com.weicai.caesar.Caesar;

/**
 * 银行卡等敏感信息加密工具类
 */
public class EncryptUtil {
    public static String encode(String msg) {

        return Caesar.encrypt(msg);
    }

    public static String decode(String msg) {
        return Caesar.decrypt(msg);
    }


    public static boolean isEncode(String msg) {
        return Caesar.isEncrypted(msg);
    }

    /**
     * 获取加密密文
     * @param msg
     * @return
     */
    public static String getEncoded(String msg) {
        return StringUtils.isNotBlank(msg) && !isEncode(msg) ? encode(msg) : msg;
    }

    /**
     * 获取解密明文
     * @param msg
     * @return
     */
    public static String getDecoded(String msg) {
        return StringUtils.isNotBlank(msg) && isEncode(msg) ? decode(msg) : msg;
    }

    public static void main(String[] args) {
        System.out.println(EncryptUtil.getDecoded("cpFy+VPr38JtcLAgRycklxAKvA1bNfnZO1qegkbgTc8="));
    }

    public static boolean isSame(String a, String b) {
        return getEncoded(StringUtils.defaultString(a)).equals(getEncoded(StringUtils.defaultString(b)));
    }
}
