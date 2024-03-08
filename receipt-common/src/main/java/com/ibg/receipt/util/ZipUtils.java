package com.ibg.receipt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableMap;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;

/**
 * ZIP操作工具类
 *
 * @author: guojianchang
 * @date: 2018年11月30日 下午4:15:59
 */
public class ZipUtils {

    private static final int DEFAULT_BUFF_SIZE = 1024;

    public static Map<String, byte[]> extractZipFiles(byte[] zipBytes) throws IOException {
        Map<String, byte[]> entryMap = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {

            for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                // 忽略文件夹
                if (!zipEntry.isDirectory()) {
                    String fileName = zipEntry.getName();
                    byte[] buf = IOUtils.toByteArray(zis);
                    entryMap.put(fileName, buf);
                }
                zis.closeEntry();
            }
        }
        return entryMap;
    }

    /** *
     *
     * @param fileMap
     * @param filePath 压缩后的文件路径及文件名称
     * @return
     * @throws IOException
     */
    public static void zipFiles(Map<String, byte[]> fileMap, File filePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filePath))) {
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                String fileName = entry.getKey();
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                try (ByteArrayInputStream in = new ByteArrayInputStream((entry.getValue()))){
                    byte[] buff = new byte[DEFAULT_BUFF_SIZE];
                    int len;
                    while ((len = in.read(buff,0 ,DEFAULT_BUFF_SIZE)) != -1) {
                        zos.write(buff, 0, len);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(),"文件压缩异常");
        }

    }

    /**
     * 多文件压缩
     */
    public static byte[] compressZipFiles(Map<String, byte[]> fileMap) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream)) {
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                String fileName = entry.getKey();
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                try (ByteArrayInputStream in = new ByteArrayInputStream((entry.getValue()))) {
                    IOUtils.copy(in, zos);
                }
                zos.closeEntry();
            }
            zos.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "文件压缩异常");
        }
    }

    /**
     * 多文件解压
     */
    public static Map<String, byte[]> decompressZipFiles(byte[] zipBytes) throws IOException {
        Map<String, byte[]> entryMap = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zis.getNextEntry()) != null && !zipEntry.isDirectory()) {
                String fileName = zipEntry.getName();
                byte[] buf = IOUtils.toByteArray(zis);
                entryMap.put(fileName, buf);
                zis.closeEntry();
            }
            zis.close();
        }
        return entryMap;
    }

    public static void main(String[] args) throws Exception {
        extractZipFiles(IOUtils.toByteArray(new FileInputStream("C:\\Users\\guojianchang\\Desktop\\20181128.zip")))
        .keySet().stream().forEach(e -> System.out.println(e));

        Map<String, byte[]> map = ImmutableMap.of("bank_net_code.sql",
                IOUtils.toByteArray(new FileInputStream("D:\\test\\bank_net_code.sql")),
            "bank_net_code.sql",
            IOUtils.toByteArray(new FileInputStream("D:\\test\\bank_net_code2.sql")));
        zipFiles(map, new File("D:\\test\\zip\\test.zip"));
    }

}
