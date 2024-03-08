/**
 * Create time 2019-05-29 9:47
 * Create by wangkai kiilin@kiilin.com
 * Copyright 2019 kiilin http://www.kiilin.com
 */

package com.ibg.receipt.sensitive;


import com.ibg.receipt.sensitive.util.SensitiveUtils;

/**
 * 脱敏字段类型
 */
public enum SensitiveType {
    /**
     * 姓名
     */
    ID_CARD_NAME,
    /**
     * 身份证号
     */
    ID_CARD_NO,

    /**
     * 手机号
     */
    MOBILE_PHONE,

    /**
     * 银行卡
     */
    BANK_CARD,;


    public static String sensitiveConvert(SensitiveType sensitiveType, String value) {
        switch (sensitiveType) {
            case MOBILE_PHONE:
                return SensitiveUtils.maskMobile(value);
            case ID_CARD_NO:
                return SensitiveUtils.maskIdCard(value);
            case BANK_CARD:
                return SensitiveUtils.maskBankCard(value);
            case ID_CARD_NAME:
                return SensitiveUtils.maskName(value);
            default:
                throw new UnsupportedOperationException("不支持的类型" + sensitiveType);
        }

    }

}
