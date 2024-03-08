package com.ibg.receipt.api.interceptor.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDupSubmit {
    int intervalSec() default 123; //间隔秒
    String key() default ""; //键值
}
