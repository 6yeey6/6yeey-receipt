package com.ibg.receipt.util.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表格信息
 *
 * @author ning
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelMapping {

    /** sheet name */
    private String name;

    /** 单元格属性列表 */
    private List<ExcelProperty> propertyList;

}
