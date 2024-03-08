package com.ibg.receipt.util;

import java.util.Arrays;
import java.util.List;

import com.ibg.receipt.base.constant.Constants;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean checkNotNull(String ... param) {
        for (String temp : param){
            if(StringUtils.isNotEmpty(temp)){
                return true;
            }
        }
        return false;
    }

    /**
     * 按分隔符转成字符串列表
     *
     * @param str
     * @param separatorChars
     * @return
     */
    public static List<String> splitToList(String str, String separatorChars) {
        String[] array = split(str, separatorChars);
        return Arrays.asList(array);
    }

    /**
     * 输入字符串为空，返回默认值
     *
     * @param str
     * @return
     */
    public static String getWithDefault(String str, String defaultStr) {
        if(isEmpty(str) || str.equals("null")){
            return defaultStr;
        }else {
            return str;
        }
    }

    /**
     * 获取到文件名的后缀
     *
     * @param fileName
     * @return
     */
    public static String parseMaterialType(String fileName) {
        if (!StringUtils.isBlank(fileName)) {
            int beginIndex = fileName.lastIndexOf(".");
            return fileName.substring(beginIndex + 1);
        }
        return null;
    }

    /**
     * 按分隔符转成字符串数组
     * @param str
     * @return
     */
    public static String[] splitToArray(String str) {
        return split(str, Constants.COMMON_SEPARATOR);
    }

    /**
     * 去掉空格及&nbsp
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        char a = (char) 0xC2;
        char b = (char) 0xA0;
        str = StringUtils.replaceChars(str, a, (char) 32);
        str = StringUtils.replaceChars(str, b, (char) 32);
        str = str.replaceAll("[\u0000]", "");
        return org.apache.commons.lang3.StringUtils.deleteWhitespace(str);
    }

    /**
     * 去掉特殊字符、表情符号、空格、"\"符号
     * @param str
     * @return
     */
    public static String filterSpecialChar(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        char a = (char) 0xC2;
        char b = (char) 0xA0;
        str = StringUtils.replaceChars(str, a, (char) 32);
        str = StringUtils.replaceChars(str, b, (char) 32);
        str = str.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff\\u0000|]", "");
        return org.apache.commons.lang3.StringUtils.deleteWhitespace(str);
    }

    public static String replaceSpecialChar(String str) {
        str = filterSpecialChar(str);
        String regEx = "[ _`~!@#$%^&*()+=|{}'.:;',\\[\\].·<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t|\b|\0|\\\\|";
        return replacePattern(str, regEx, StringUtils.EMPTY);
    }

    public static void main(String[] args) {
        String s = "河南平顶山市汝州市\b汝南办()事#处/焦:村六\\组";
        String s2 = "河南平顶山市汝州市 东头乡&<\n咕咚组";
        s = filterSpecialChar(s);
        String regEx = "[ _`~!@#$%^&*()+=|{}'.:;',\\[\\].·<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t|\b|\0|\\\\|";
        String regEx2 = "[&< ]|\n|\r";
        // Pattern p = Pattern.compile(regEx);
        // Matcher m = p.matcher(s);
        String ss = replacePattern(s2, regEx2, StringUtils.EMPTY);
        System.out.println(s2);
        System.out.println(ss);
    }
}
