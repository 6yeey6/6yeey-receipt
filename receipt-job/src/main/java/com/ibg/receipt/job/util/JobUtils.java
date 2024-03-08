package com.ibg.receipt.job.util;

import org.springframework.util.ClassUtils;

public class JobUtils {

    /** Job运行Key前缀 */
    public static final String JOB_RUNNING_KEY_PREFIX = "JOB_RUNNING_";
    /** Job运行Key超时时间 */
    public static final long JOB_RUNNING_KEY_EXPIRED = 7 * 24 * 60 * 60;
    public static final int JOB_RUNNING_KEY_EXPIRED_INT = 7 * 24 * 60 * 60;

    /**
     * 获取Job运行的Key
     *
     * @param classSimpleName
     * @return
     */
    public static String getJobRunningKey(String classSimpleName) {
        return JOB_RUNNING_KEY_PREFIX + classSimpleName;
    }

    public static String getClassSimpleName(Class clazz) {
        return ClassUtils.getUserClass(clazz).getSimpleName();
    }
}
