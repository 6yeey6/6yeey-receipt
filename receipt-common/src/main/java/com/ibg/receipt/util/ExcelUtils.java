package com.ibg.receipt.util;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibg.receipt.base.exception.ExceptionUtils;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2020/10/16 16:00 <br>
 * @description ExcelUtils <br>
 */
public class ExcelUtils {
    private static String titlefontName = "宋体";
    private static short titleSize = (short) 13;
    private static Color titleFontColor = Color.BLACK;
    private static Color titleForegroudColor = new Color(170, 230, 255);

    private static String detailfontName = "宋体";
    private static short detailSize = (short) 13;
    private static Color detailFontColor = Color.BLACK;
    private static Color detailForegroudColor = new Color(156, 195, 230);

    /**
     * 生成Excel
     *
     * @param header    报表头
     * @param details   报表内容
     * @param sheetName 表单名称
     * @return XSSFWorkbook
     */
    public static XSSFWorkbook exportExcel(List<String> header, List<List<String>> details, String sheetName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        int rowNum = 0;
        CellStyle titleCellType = excelTitleCellStyle(workbook);
        CellStyle detailCellType = excelDetailCellStyle(workbook);

        XSSFRow row = sheet.createRow(rowNum++);
        for (int i = 0; i < header.size(); i++) {
            sheet.setColumnWidth(i, (header.get(i).length() * 2 + 1) * 256);
            Cell cell = row.createCell(i);
            cell.setCellStyle(titleCellType);
            cell.setCellValue(header.get(i));
        }
        for (int i = 0; i < details.size(); i++) {
            List<String> detail = details.get(i);
            row = sheet.createRow(rowNum++);
            for (int j = 0; j < detail.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(detailCellType);
                cell.setCellValue(detail.get(j));
            }
        }
        return workbook;
    }

    /**
     * 生成Excel字节
     *
     * @param header    报表头
     * @param details   报表内容
     * @param sheetName 表单名称
     * @return XSSFWorkbook
     */
    public static byte[] exportExcelBytes(List<String> header, List<List<String>> details, String sheetName) {
        return excelToByte(exportExcel(header, details, sheetName));
    }

    /**
     * Excel转byte
     */
    public static byte[] excelToByte(XSSFWorkbook workBook) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            workBook.write(os);
            byte[] byteArray = os.toByteArray();
            os.reset();
            os.close();
            return byteArray;
        } catch (Exception e) {
            String message = String.format("理赔名单报表实体写入流中异常message:%s", e.getMessage());
            throw ExceptionUtils.commonError(message);
        }
    }

    public static byte[] excelToByte(SXSSFWorkbook workBook) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            workBook.write(os);
            byte[] byteArray = os.toByteArray();
            os.reset();
            os.close();
            return byteArray;
        } catch (Exception e) {
            String message = String.format("workBook写入流中异常message:%s", e.getMessage());
            throw ExceptionUtils.commonError(message);
        }
    }

    private static CellStyle excelTitleCellStyle(XSSFWorkbook workbook) {

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //左右居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //设置背景色
        cellStyle.setFillForegroundColor(new XSSFColor(titleForegroudColor));
        //填充模式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(titleFontColor));
        font.setFontHeightInPoints(titleSize);
        font.setFontName(titlefontName);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private static CellStyle excelDetailCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        //左右居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        //设置背景色
        cellStyle.setFillForegroundColor(new XSSFColor(detailForegroudColor));
        //填充模式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = workbook.createFont();
        font.setBold(false);
        font.setColor(new XSSFColor(detailFontColor));
        font.setFontHeightInPoints(detailSize);
        font.setFontName(detailfontName);
        cellStyle.setFont(font);
        return cellStyle;
    }

}
