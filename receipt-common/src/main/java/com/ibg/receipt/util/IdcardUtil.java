package com.ibg.receipt.util;


import java.util.Calendar;
import java.util.Date;

/**
 * 身份证相关工具类<br>
 * see https://www.oschina.net/code/snippet_1611_2881
 */
public class IdcardUtil {

    /**
     * 根据出生日期年龄
     */
    public static int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR); // 当前年份
        int monthNow = cal.get(Calendar.MONTH); // 当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); // 当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth; // 计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;// 当前日期在生日之前，年龄减一
            } else {
                age--;// 当前月份在生日之前，年龄减一

            }
        }
        return age;
    }

    /**
     * 根据身份证号计算年龄
     * 不支持第一代15位数身份证
     */
    public static int getAge(String idCardNo) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR); // 当前年份
        int monthNow = cal.get(Calendar.MONTH); // 当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); // 当前日期
        String BirthString = idCardNo.substring(6, 13);
        Date parse = DateUtils.parse(BirthString, DateUtils.DATE_FORMAT_PATTERN_NO_HYPHEN);
        cal.setTime(parse);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth; // 计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth)
                    age--;// 当前日期在生日之前，年龄减一
            } else {
                age--;// 当前月份在生日之前，年龄减一

            }
        }
        return age;
    }

}
