package com.ibg.receipt.util.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单元格属性
 *
 * @author ning
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelProperty {

    /** 单元格标题 */
    private String name;

    /** 单元格取值的字段名 */
    private String column;

    /** 单元格宽度 */
    private Short width;

    /** 日期格式 */
    private String dateFormat;

    /** 排序 */
    private int order;

}
