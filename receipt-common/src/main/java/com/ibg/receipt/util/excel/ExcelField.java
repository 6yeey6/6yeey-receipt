package com.ibg.receipt.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义单元格属性
 *
 * @author ning
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {

    /** 单元格标题，默认为当前字段名 */
    String value() default "";

    /** 单元格取值的字段名，默认为当前字段名 */
    String name() default "";

    /** 单元格宽度，默认-1（自动计算列宽） */
    short width() default -1;

    /** 日期格式，如：yyyy-MM-dd */
    String dateFormat() default "";

    /** 排序，从小到大排序 */
    int order() default 0;

}
