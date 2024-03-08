package com.ibg.receipt.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.ibg.receipt.base.exception.ServiceException;

public class ValidatorUtils {

    /**
     * 中国公民身份证号码最小长度
     */
    private static final int CHINA_ID_MIN_LENGTH = 15;
    /**
     * 中国公民身份证号码最大长度
     */
    private static final int CHINA_ID_MAX_LENGTH = 18;
    public final static Pattern EMAIL = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])",
            Pattern.CASE_INSENSITIVE);
    /**
     * 数字
     */
    public final static Pattern NUMBERS = Pattern.compile("\\d+");

    public static boolean isEmail(CharSequence content) {
        return EMAIL.matcher(content).matches();
    }

    public static void main(String[] args) {
//        Map<String, String> map = getBirAgeSex("31023019710405477X");
//        System.out.println(map);
        String s = getGenderByIdCardNo("31023019710405477X");
        System.out.println(s);

        Map<String, List<String>> ss = new HashMap<String, List<String>>();
        ss.put("szss", Lists.newArrayList("xd", "zj"));
        ss.put("YN_TRUST", Lists.newArrayList("zhaosong", "xudong01"));
        System.out.println(JsonUtils.toJson(ss));
    }

    /**
     * 根据身份编号获取性别，只支持15或18位身份证号码
     *
     * @param idCardNo
     *            身份编号
     * @return 性别(0 : 男 ， 1 : 女)
     */
    public static String getGenderByIdCardNo(String idCardNo) {
        int len = idCardNo.length();
        if (len == CHINA_ID_MIN_LENGTH || len == CHINA_ID_MAX_LENGTH) {
            if (isMatch(NUMBERS, idCardNo)) {
                Integer c = 0;
                if (idCardNo.length() == 15) {
                    c = idCardNo.charAt(14) - 48;
                } else {
                    c = idCardNo.charAt(16) - 48;
                }
                if (c % 2 == 1) {
                    return "0";
                } else {
                    return "1";
                }
            } else {
                throw new ServiceException("身份证号包含非法字符");
            }
        } else {
            throw new ServiceException("身份证长度必须为15或者18位");
        }
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern
     *            模式
     * @param content
     *            内容
     * @return 正则为null或者""则不检查，返回false，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, String content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        if (content.length() == 18) {
            content = content.substring(0, content.length() - 1);
        }
        return pattern.matcher(content).matches();
    }
}
