package com.ibg.receipt.util.execltoimage;

import com.ibg.receipt.base.exception.ServiceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * width:pix=getColumnWidthInPixels*1.15
 * height:pix=getHeightInPoints*96/72
 * <p>
 * 本示例用来读取Excel报表文件，并力图将报表无差别转化成PNG图片
 * 使用POI读取Excel各项数据
 * 使用JAVA 2D绘制PNG
 * 本示例基本实现了常见Excel的所有样式输出，但Office2007以后的版本添加了条件样式功能，，所以无法实现
 * 今后可以通过关键字标注等方法，在Excel中需要加入条件样式的单元格用注解标明，使用JAVA计算得出样因为POI的API无法读取条件样式式再绘制出来* 可以导出图片
 * <p>
 */
public class DrawFromExcel {

    /**
     * excel转图片
     *
     * @throws IOException,InvalidFormatException 异常
     */
    public static Map<String, Object> excelToImage(XSSFWorkbook wb, String name) {
        return excelToImage(wb, name, true);
    }

    /**
     * excel转图片
     *
     * @throws IOException,InvalidFormatException 异常
     */
    public static Map<String, Object> excelToImage(XSSFWorkbook wb, String name, boolean fixWidth) {
        // 图片宽度
        int imageWidth;
        // 图片高度
        int imageHeight;
        //第一个工作表
        Sheet sheet = wb.getSheetAt(0);

        // 获取整个sheet中合并单元格组合的集合
        List<CellRangeAddress> rangeAddress = sheet.getMergedRegions();

        //检查区域内是否存在数据
        int rowSize = sheet.getPhysicalNumberOfRows();
        int colSize = sheet.getRow(0).getPhysicalNumberOfCells();
        if (rowSize == 0 || colSize == 0) {
            return new HashMap<>();
        }
        // 遍历需要扫描的区域
        UserCell[][] cells = new UserCell[rowSize][colSize];
        int[] rowPixPos = new int[rowSize + 1];
        rowPixPos[0] = 0;
        int[] colPixPos = new int[colSize + 1];
        colPixPos[0] = 0;

        float height = 0f;
        float width = 0f;
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                cells[i][j] = new UserCell();
                cells[i][j].setCell(sheet.getRow(i).getCell(j));
                cells[i][j].setRow(i);
                cells[i][j].setCol(j);
                //行列不可以隐藏
                boolean ifShow = !(sheet.isColumnHidden(j) || sheet.getRow(i).getZeroHeight());
                cells[i][j].setShow(ifShow);
                // 计算所求区域宽度 如果该单元格是隐藏的，则置宽度为0
                float widthPix = !ifShow ? 0 : (fixWidth ? 140 : sheet.getColumnWidthInPixels(j));
                if (i == 0) {
                    width += widthPix;
                }
                colPixPos[j + 1] = (int) (widthPix * 1.15 + colPixPos[j]);
            }
            // 计算所求区域高度 行序列不能隐藏
            boolean ifShow = !sheet.getRow(i).getZeroHeight();
            // 如果该单元格是隐藏的，则置高度为0
            float heightPoint = !ifShow ? 0 : sheet.getRow(i).getHeightInPoints();
            height += heightPoint;
            rowPixPos[i + 1] = (int) (heightPoint * 96 / 72) + rowPixPos[i];
        }
        imageHeight = (int) (height * 96 / 72);
        imageWidth = (int) (width * 115 / 100);

        List<Grid> grids = getGrids(wb, rangeAddress, rowSize, colSize, cells, rowPixPos, colPixPos, new HashMap<>());
        // 绘图
        try {
            return draw(imageWidth, imageHeight, grids, name);
        } catch (IOException e) {
            throw new ServiceException("execl转image出现异常", e);
        }
    }

    /**
     * 根据表格制作网格
     *
     * @param wb           excel
     * @param rangeAddress 合并单元格组合
     * @param rowSize      行大小
     * @param colSize      列大小
     * @param cells        单元格
     * @param rowPixPos    需要扫描的行大小
     * @param colPixPos    需要扫描的列大小
     * @return 网格数据
     */
    private static List<Grid> getGrids(Workbook wb, List<CellRangeAddress> rangeAddress, int rowSize, int colSize, UserCell[][] cells, int[] rowPixPos, int[] colPixPos, Map<String, PictureData> dataMap) {
        List<Grid> grids = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                //当前表格
                Cell cell = cells[i][j].getCell();
                Grid grid = new Grid();
                // 设置坐标和宽高
                grid.setX(colPixPos[j]);
                grid.setY(rowPixPos[i]);
                grid.setWidth(colPixPos[j + 1] - colPixPos[j]);
                grid.setHeight(rowPixPos[i + 1] - rowPixPos[i]);
                grid.setRow(cells[i][j].getRow());
                grid.setCol(cells[i][j].getCol());
                grid.setShow(cells[i][j].isShow());
                // 判断是否为合并单元格
                int[] isInMergedStatus = isInMerged(grid.getRow(), grid.getCol(), rangeAddress);
                if (cell == null || (isInMergedStatus[0] == 0 && isInMergedStatus[1] == 0)) {
                    continue;
                } else if (isInMergedStatus[0] != -1 && isInMergedStatus[1] != -1) {
                    // 此单元格是合并单元格，并且属于第一个单元格，则需要调整网格大小
                    int lastRowPos = Math.min(isInMergedStatus[0], rowSize - 1);
                    int lastColPos = Math.min(isInMergedStatus[1], colSize - 1);
                    grid.setWidth(colPixPos[lastColPos + 1] - colPixPos[j]);
                    grid.setHeight(rowPixPos[lastRowPos + 1] - rowPixPos[i]);
                }
                // 单元格背景颜色
                CellStyle cs = cell.getCellStyle();
                if (cs.getFillPatternEnum() == FillPatternType.SOLID_FOREGROUND) {
                    grid.setBgColor(cell.getCellStyle().getFillForegroundColorColor());
                }
                // 设置字体
                org.apache.poi.ss.usermodel.Font font = wb.getFontAt(cs.getFontIndex());
                //设置为宋体
                font.setFontName("宋体");
                grid.setFont(font);
                // 设置字体前景色
                if (font instanceof XSSFFont) {
                    XSSFFont xf = (XSSFFont) font;
                    grid.setFtColor(xf.getXSSFColor());
                }
                // 设置文本
                String strCell;
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        strCell = DateFormatUtils.format(cell.getDateCellValue(), "yyyy-MM-dd");
                    } else {
                        NumberFormat nf = NumberFormat.getInstance();
                        strCell = String.valueOf(nf.format(cell.getNumericCellValue())).replace(",", "");
                    }
                } else if (cell.getCellTypeEnum() == CellType.STRING) {
                    strCell = String.valueOf(cell.getStringCellValue());
                } else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
                    strCell = String.valueOf(cell.getBooleanCellValue());
                } else if (cell.getCellTypeEnum() == CellType.ERROR) {
                    strCell = "错误类型";
                } else {
                    strCell = "";
                }
                if (cell.getCellStyle().getDataFormatString().contains("0.00%")) {
                    try {
                        double dbCell = Double.parseDouble(strCell);
                        strCell = new DecimalFormat("#.00").format(dbCell * 100) + "%";
                    } catch (NumberFormatException ignored) {
                    }
                }
                grid.setText(strCell.matches("\\w*\\.0") ? strCell.substring(0, strCell.length() - 2) : strCell);
                grids.add(grid);
            }
        }
        return grids;
    }

    /**
     * 绘图
     *
     * @param imageWidth  图片宽度
     * @param imageHeight 图片高度
     * @param grids       绘制图片的内容
     */
    public static Map<String, Object> draw(int imageWidth, int imageHeight, List<Grid> grids, String name) throws IOException {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        // 平滑字体
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, imageWidth, imageHeight);
        // 绘制表格
        for (Grid g : grids) {
            if (!g.isShow()) {
                continue;
            }
            // 绘制背景色
            g2d.setColor(g.getBgColor() == null ? Color.white : g.getBgColor());
            g2d.fillRect(g.getX(), g.getY(), g.getWidth(), g.getHeight());
            // 绘制边框
            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER, 5f, null, 0f));
            g2d.drawRect(g.getX(), g.getY(), g.getWidth(), g.getHeight());
            // 绘制文字,居中显示
            g2d.setColor(g.getFtColor());
            Font font = g.getFont();
            FontMetrics fm = g2d.getFontMetrics(font);
            // 获取将要绘制的文字宽度
            int strWidth = fm.stringWidth(g.getText());
            g2d.setFont(font);
            // 图片
            if (g.getData() != null && g.getData().length > 0) {
                InputStream inputStream = new ByteArrayInputStream(g.getData());
                BufferedImage bg = ImageIO.read(inputStream);
                g2d.drawImage(bg.getScaledInstance(g.getWidth(), g.getHeight(), Image.SCALE_DEFAULT),
                    g.getX(),
                    g.getY(), null);
            } else {// 文字
                g2d.drawString(g.getText(),
                    g.getX() + (g.getWidth() - strWidth) / 2,
                    g.getY() + (g.getHeight() - font.getSize()) / 2 + font.getSize());
            }

        }
        g2d.dispose();
        //生成图片
        String imgUrl = FileUtils.getTempDirectory() + File.separator + name + ".png";
        ImageIO.write(image, "png", new File(imgUrl));
        File file = new File(imgUrl);
        byte[] byteArray = FileUtils.readFileToByteArray(file);
        String base64 = Base64.getEncoder().encodeToString(byteArray);
        String imgMd5 = DigestUtils.md5Hex(new FileInputStream(imgUrl));
        Map<String, Object> imageMap = new HashMap<String, Object>();
        imageMap.put("base64", base64);
        imageMap.put("md5", imgMd5);
        return imageMap;
    }


    /**
     * 判断Excel中的单元格是否为合并单元格
     *
     * @param row          当前行号
     * @param col          当前列号
     * @param rangeAddress 整个sheet中合并单元格组合的集合
     * @return 如果不是合并单元格返回{-1,-1},如果是合并单元格并且是一个单元格返回{lastRow,lastCol},
     * 如果是合并单元格并且不是第一个格子返回{0,0}
     */
    private static int[] isInMerged(int row, int col, List<CellRangeAddress> rangeAddress) {
        int[] isInMergedStatus = {-1, -1};
        for (CellRangeAddress cra : rangeAddress) {
            if (row == cra.getFirstRow() && col == cra.getFirstColumn()) {
                isInMergedStatus[0] = cra.getLastRow();
                isInMergedStatus[1] = cra.getLastColumn();
                return isInMergedStatus;
            }
            if (row >= cra.getFirstRow() && row <= cra.getLastRow() && col >= cra.getFirstColumn() && col <= cra.getLastColumn()) {
                isInMergedStatus[0] = 0;
                isInMergedStatus[1] = 0;
                return isInMergedStatus;
            }
        }
        return isInMergedStatus;
    }
}

