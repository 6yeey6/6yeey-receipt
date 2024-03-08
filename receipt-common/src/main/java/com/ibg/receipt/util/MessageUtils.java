package com.ibg.receipt.util;

/**
 * <pre>
 * 几个info表的message字段的字符串内容处理工具类
 * Created by Binary Wang on 2017-6-19.
 * </pre>
 *
 * @author BinaryWang
 */
public class MessageUtils {
    /**
     * message字段的长度
     */
    private static final int MESSAGE_COLUMN_LENGTH = 128;

    /**
     * <pre>
     * 截断字符串，以适应数据库字段长度
     * 因为如果内容过长，会导致保存数据库异常
     * </pre>
     *
     * @param length 最大长度
     * @param msg    原消息文本内容
     * @return 截取后的字符串
     */
    public static String truncate(String msg, int length) {
        if (StringUtils.isBlank(msg)) {
            return null;
        }

        if (msg.length() > length) {
            return StringUtils.truncate(msg, length - 3) + "...";
        }

        return msg;
    }

    /**
     * <pre>
     * 截断字符串，最大长度为128
     * </pre>
     *
     * @param msg 原消息文本内容
     * @return 截取后的字符串
     */
    public static String truncate(String msg) {
        return truncate(msg, MESSAGE_COLUMN_LENGTH);
    }
}
