package com.ibg.receipt.util;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感数据脱敏工具类
 */
public class Desensitization {

    /**
     * 身份证号脱敏
     *
     * @param idCard
     * @return
     */
    public static String idCardDesensitization(String idCard) {
        if (StringUtils.isNotEmpty(idCard)) {
            // 身份证号脱敏规则一：保留前六后三
            if (idCard.length() == 15) {
                idCard = idCard.replaceAll("(\\w{6})\\w*(\\w{3})", "$1******$2");
            } else if (idCard.length() == 18) {
                idCard = idCard.replaceAll("(\\w{6})\\w*(\\w{3})", "$1*********$2");
            }
            // 身份证号脱敏规则二：保留前三后四
            // idCard = idCard.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
        }
        return idCard;
    }

    /**
     * 手机号码脱敏
     *
     * @param mobilePhone
     * @return
     */
    public static String mobilePhoneDesensitization(String mobilePhone) {
        // 手机号码保留前三后四
        if (StringUtils.isNotEmpty(mobilePhone)) {
            mobilePhone = mobilePhone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobilePhone;
    }

    /**
     * 电子邮箱脱敏
     *
     * @param email
     * @return
     */
    public static String emailDesensitization(String email) {
        // 电子邮箱隐藏@前面的3个字符
        if (StringUtils.isEmpty(email)) {
            return email;
        }
        String encrypt = email.replaceAll("(\\w+)\\w{3}@(\\w+)", "$1***@$2");
        if (email.equalsIgnoreCase(encrypt)) {
            encrypt = email.replaceAll("(\\w*)\\w{1}@(\\w+)", "$1*@$2");
        }
        return encrypt;
    }

    /**
     * 银行账号脱敏
     *
     * @param acctNo
     * @return
     */
    public static String acctNoDesensitization(String acctNo) {
        // 银行账号保留前六后四
        if (StringUtils.isNotEmpty(acctNo)) {
            String regex = "(\\w{6})(.*)(\\w{4})";
            Matcher m = Pattern.compile(regex).matcher(acctNo);
            if (m.find()) {
                String rep = m.group(2);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < rep.length(); i++) {
                    sb.append("*");
                }
                acctNo = acctNo.replaceAll(rep, sb.toString());
            }
        }
        return acctNo;
    }

    /**
     * 客户名称脱敏
     *
     * @param custName
     * @return
     */
    public static String custNameDesensitization(String custName) {
        // 规则说明：
        // 姓名：字符长度小于5位；企业名称：字符长度大于等于5位。
        // 姓名规则
        // 规则一：1个字则不脱敏，如"张"-->"张"
        // 规则二：2个字则脱敏第二个字，如"张三"-->"张*"
        // 规则三：3个字则脱敏第二个字，如"张三丰"-->"张*丰"
        // 规则四：4个字则脱敏中间两个字，如"易烊千玺"-->"易**玺"
        // 企业名称规则：
        // 从第4位开始隐藏，最多隐藏6位。

        if (StringUtils.isNotEmpty(custName)) {
            char[] chars = custName.toCharArray();
            if (chars.length < 5) {// 表示姓名
                if (chars.length > 1) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < chars.length - 2; i++) {
                        sb.append("*");
                    }
                    custName = custName.replaceAll(custName.substring(1, chars.length - 1), sb.toString());
                }
            } else {// 企业名称
                int start = 4;
                // 第一部分
                String str1 = custName.substring(0, start);
                // 第二部分
                String str2 = "";
                if (chars.length == 5) {
                    str2 = "*";
                } else if (chars.length == 6) {
                    str2 = "**";
                } else if (chars.length == 7) {
                    str2 = "***";
                } else if (chars.length == 8) {
                    str2 = "****";
                } else if (chars.length == 9) {
                    str2 = "*****";
                } else {
                    str2 = "******";
                }
                // 通过计算得到第三部分需要从第几个字符截取
                int subIndex = start + str2.length();
                // 第三部分
                String str3 = custName.substring(subIndex);

                StringBuffer sb = new StringBuffer();
                sb.append(str1);
                sb.append(str2);
                sb.append(str3);
                custName = sb.toString();
            }
        }
        return custName;
    }

    /**
     * 家庭地址脱敏
     *
     * @param address
     * @return
     */
    public static String addressDesensitization(String address) {
        // 规则说明：从第4位开始隐藏，隐藏8位。
        if (StringUtils.isNotEmpty(address)) {
            char[] chars = address.toCharArray();
            if (chars.length > 11) {// 由于需要从第4位开始，隐藏8位，因此数据长度必须大于11位
                // 获取第一部分内容
                String str1 = address.substring(0, 4);
                // 获取第二部分
                String str2 = "********";
                // 获取第三部分
                String str3 = address.substring(12);
                StringBuffer sb = new StringBuffer();
                sb.append(str1);
                sb.append(str2);
                sb.append(str3);
                address = sb.toString();
            }
        }
        return address;
    }

}