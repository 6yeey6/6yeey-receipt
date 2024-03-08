package com.ibg.receipt.util;

public class SignUtils {

    /**
     * 生成签名
     * 
     * @param obj
     * @param key
     * @return
     */
    public String sign(Object obj, String key) {
        return MD5Utils.md5Hex(JsonUtils.toJson(obj) + key);
    }

    /**
     * 检查签名
     * 
     * @param obj
     *            不包含sign
     * @param sign
     * @param key
     * @return
     */
    public boolean checkSign(Object obj, String sign, String key) {
        return sign.equals(sign(obj, key));
    }

}
