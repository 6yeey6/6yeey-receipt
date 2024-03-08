package com.ibg.receipt.sensitive.util;

import com.ibg.receipt.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Description: 脱敏工具类
 *
 * @author lining
 * @date 20-7-21
 */
public class SensitiveUtils {

    // 姓名脱敏
    public static String maskName(final String initName) {
        if (StringUtils.isBlank(initName)) {
            return initName;
        }

        if (EncryptUtil.isEncode(initName)) {
            return initName;
        }
        char[] r = initName.trim().toCharArray();
        String resultName = "";
        if(r.length == 1){
            resultName = initName;
        } else if(r.length == 2){
            resultName =  r[0]+"*";
        }else {
            String star = "";
            for (int i = 0; i < r.length-2; i++) {
                star=star+"*";
            }
            resultName = r[0]+star+r[r.length-1];
        }
        return resultName;
    }


    // 身份证号脱敏
    public static String maskIdCard(final String initIdCard) {
        if (StringUtils.isBlank(initIdCard)) {
            return initIdCard;
        }
        if (EncryptUtil.isEncode(initIdCard)) {
            return initIdCard;
        }

        int len = initIdCard.length();

        if (len <= 10) {
            return initIdCard;
        }
        return initIdCard.substring(0, 6) + StringUtils.repeat("*", len - 10) + initIdCard.substring(len - 4);
    }

    // 手机号脱敏
    public static String maskMobile(final String initMobile) {
        if (StringUtils.isBlank(initMobile)) {
            return initMobile;
        }

        if (EncryptUtil.isEncode(initMobile)) {
            return initMobile;
        }

        if (initMobile.length() != 11) {
            return initMobile;
        }
        return initMobile.trim().substring(0, 3) + "****" + initMobile.trim().substring(7);
    }

    // 银行卡号脱敏
    public static String maskBankCard(final String initBankCard) {
        if (StringUtils.isBlank(initBankCard)) {
            return initBankCard;
        }

        if (EncryptUtil.isEncode(initBankCard)) {
            return initBankCard;
        }

        int len = initBankCard.length();

        if (len <= 8) {
            return initBankCard;
        }
        return initBankCard.substring(0, 4) + StringUtils.repeat("*", len - 8) + initBankCard.substring(len - 4);
    }
}
