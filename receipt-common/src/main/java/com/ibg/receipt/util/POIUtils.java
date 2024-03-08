package com.ibg.receipt.util;

import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.util.excel.ExcelProperty;
import com.ibg.receipt.util.excel.ExcelMapping;
import com.ibg.receipt.util.excel.ExcelMappingFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * POI工具类
 */
@Slf4j
public class POIUtils {

    private static final int DEFAULT_WINDOW_SIZE = 100;

    private CellStyle mHeaderCellStyle = null;
    private CellStyle mStringCellStyle = null;
    private Class<?> mClass = null;

    public static POIUtils initInstance(Class<?> clazz) {
        return new POIUtils(clazz);
    }

    private POIUtils(Class<?> clazz) {
        this.mClass = clazz;
    }

    /**
     * 获取Workbook
     *
     * @param is
     * @return
     */
    public static Workbook getWorkbook(InputStream is) {
        Workbook book = null;
        try {
            book = new XSSFWorkbook(is);
        } catch (Exception e) {
            try {
                book = new HSSFWorkbook(is);
            } catch (IOException ex) {
                log.error("获取Workbook失败", ex);
                throw ExceptionUtils.commonError("获取Workbook失败");
            }
        }
        return book;
    }

    public static SXSSFWorkbook newSXSSFWorkbook() {
        return new SXSSFWorkbook(DEFAULT_WINDOW_SIZE);
    }

    public static SXSSFSheet newSXSSFSheet(SXSSFWorkbook wb, String sheetName) {
        return wb.createSheet(sheetName);
    }

    public static SXSSFRow newSXSSFRow(SXSSFSheet sheet, int index) {
        return sheet.createRow(index);
    }

    public static SXSSFCell newSXSSFCell(SXSSFRow row, int index) {
        return row.createCell(index);
    }

    public static void setColumnWidth(SXSSFSheet sheet, int index, Short width, String value) {
        boolean widthNotHaveConfig = (null == width || width == -1);
        if (widthNotHaveConfig && StringUtils.isNotBlank(value)) {
            sheet.setColumnWidth(index, (short) (value.length() * 2048));
        } else {
            width = widthNotHaveConfig ? 200 : width;
            sheet.setColumnWidth(index, (short) (width * 35.7));
        }
    }

    /**
     * 生成Workbook
     *
     * @param data      数据
     * @param excelName Excel名称
     */
    public SXSSFWorkbook generateWorkBook(List<?> data, String excelName) {
        try {
            ExcelMapping excelMapping = ExcelMappingFactory.get(mClass);
            excelMapping.setName(excelName);
            SXSSFWorkbook workbook = newSXSSFWorkbook();
            List<ExcelProperty> propertyList = excelMapping.getPropertyList();
            SXSSFSheet sheet = generateXlsxHeader(workbook, propertyList, excelMapping.getName());
            if (null != data && data.size() > 0) {
                for (int i = 1; i <= data.size(); i++) {
                    SXSSFRow bodyRow = POIUtils.newSXSSFRow(sheet, i);
                    for (int j = 0; j < propertyList.size(); j++) {
                        SXSSFCell cell = POIUtils.newSXSSFCell(bodyRow, j);
                        buildCellValueByExcelProperty(cell, data.get(i - 1), propertyList.get(j), workbook);
                    }
                }
            }
            return workbook;
        } catch (Exception e) {
            log.error("生成Workbook失败", e);
            throw ExceptionUtils.commonError("生成Workbook失败");
        }
    }

    public SXSSFSheet generateXlsxHeader(SXSSFWorkbook workbook, List<ExcelProperty> propertyList, String sheetName) {
        SXSSFSheet sheet = newSXSSFSheet(workbook, sheetName);
        SXSSFRow headerRow = newSXSSFRow(sheet, 0);
        for (int i = 0; i < propertyList.size(); i++) {
            ExcelProperty property = propertyList.get(i);
            SXSSFCell cell = newSXSSFCell(headerRow, i);

            setColumnWidth(sheet, i, property.getWidth(), property.getColumn());

            cell.setCellStyle(getHeaderCellStyle(workbook));
            String headerColumnValue = property.getColumn();
            cell.setCellValue(headerColumnValue);
        }
        return sheet;
    }

    public CellStyle getHeaderCellStyle(SXSSFWorkbook wb) {
        if (null == mHeaderCellStyle) {
            mHeaderCellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            mHeaderCellStyle.setFillForegroundColor((short) 12);
            /*mHeaderCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            mHeaderCellStyle.setBorderTop(CellStyle.BORDER_DOTTED);
            mHeaderCellStyle.setBorderRight(CellStyle.BORDER_DOTTED);
            mHeaderCellStyle.setBorderBottom(CellStyle.BORDER_DOTTED);
            mHeaderCellStyle.setBorderLeft(CellStyle.BORDER_DOTTED);
            mHeaderCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            mHeaderCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
            mHeaderCellStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
            font.setColor(HSSFColor.WHITE.index);*/
            mHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            mHeaderCellStyle.setBorderTop(BorderStyle.DOTTED);
            mHeaderCellStyle.setBorderRight(BorderStyle.DOTTED);
            mHeaderCellStyle.setBorderBottom(BorderStyle.DOTTED);
            mHeaderCellStyle.setBorderLeft(BorderStyle.DOTTED);
            mHeaderCellStyle.setAlignment(HorizontalAlignment.LEFT);
            mHeaderCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
            mHeaderCellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
            font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            mHeaderCellStyle.setFont(font);
            DataFormat dataFormat = wb.createDataFormat();
            mHeaderCellStyle.setDataFormat(dataFormat.getFormat("@"));
        }
        return mHeaderCellStyle;
    }

    private void buildCellValueByExcelProperty(SXSSFCell cell, Object entity, ExcelProperty property,
        SXSSFWorkbook wb) {
        Object cellValue;
        try {
            cellValue = BeanUtils.getProperty(entity, property.getName());
        } catch (Exception e) {
            log.error("获取属性值失败");
            throw ExceptionUtils.commonError("获取属性值失败");
        }
        if (null != cellValue) {
            String dateFormat = property.getDateFormat();
            if (StringUtils.isNoneBlank(dateFormat)) {
                if (cellValue instanceof Date) {
                    cell.setCellValue(DateFormatUtils.format((Date) cellValue, dateFormat));
                } else if (cellValue instanceof String) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date parse = format.parse(String.valueOf(cellValue));
                        cell.setCellValue(DateFormatUtils.format(parse, dateFormat));
                    } catch (ParseException e) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",
                                Locale.ENGLISH);
                            Date parse = format.parse((String) cellValue);
                            cell.setCellValue(DateFormatUtils.format(parse, dateFormat));
                        } catch (ParseException e1) {
                            throw ExceptionUtils.commonError("日期解析失败");
                        }
                    }
                    return;
                }
            }
            if (null == mStringCellStyle) {
                mStringCellStyle = wb.createCellStyle();
                mStringCellStyle.setDataFormat(wb.createDataFormat().getFormat("@"));
            }
            cell.setCellStyle(mStringCellStyle);
            cell.setCellValue(String.valueOf(cellValue));
        }
    }

}
