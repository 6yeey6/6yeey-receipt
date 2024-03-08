package com.ibg.receipt.util;

import java.io.*;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.springframework.beans.factory.annotation.Value;

/**
 * zip4j 流操作类
 *
 * @author zangyunfei
 */
@Slf4j
public class Zip4jUtils extends CompressUtil {
    /**
     * 多个文件byte流，转成base64数组
     *
     * @param map
     * @return
     */
    public static String filesToBase64(Map<String, byte[]> map) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(
                    byteArrayOutputStream)) {
            for (Entry<String, byte[]> entry : map.entrySet()) {
                Zip4jUtils.addFileToZip(entry.getKey(), entry.getValue(),
                    zipOutputStream);
            }
            Zip4jUtils.closeZipOutputStream(zipOutputStream);
            byte[] zipData = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(zipData);

        } catch (Exception e) {
            log.error("filesToBase64 error", e);
            return null;
        }
    }

    /**
     * 多个文件byte流，转成base64数组
     *
     * @param map
     * @return
     */
    public static byte[] filesToByte(Map<String, byte[]> map) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(
                    byteArrayOutputStream)) {
            for (Entry<String, byte[]> entry : map.entrySet()) {
                Zip4jUtils.addFileToZip(entry.getKey(), entry.getValue(),
                    zipOutputStream);
            }
            Zip4jUtils.closeZipOutputStream(zipOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
        	log.error("filesToByte error", e);
            return null;
        }
    }

    /**
     * 多个文件byte流，转成base64数组,
     *
     * @param map
     * @param password
     * @return
     */
    public static byte[] filesToZipByte(Map<String, byte[]> map, String password) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(
                    byteArrayOutputStream)) {
            for (Entry<String, byte[]> entry : map.entrySet()) {
                if (StringUtils.isEmpty(password)) {
                    Zip4jUtils.addFileToZip(entry.getKey(), entry.getValue(),
                        zipOutputStream);
                } else {
                    Zip4jUtils.addFileToZip(entry.getKey(), entry.getValue(),
                        password, zipOutputStream);
                }
            }
            Zip4jUtils.closeZipOutputStream(zipOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
        	log.error("filesToByte error", e);
            return null;
        }
    }



    /**
     * 将内存文件写入zip内。注意：最后必须调用closeZipOutputStream关闭输出流，或者手动关闭
     *
     * @param fileName
     *        文件名
     * @param data
     *        文件数据
     * @param password
     *        密码
     * @param zipOutputStream
     * @throws ZipException
     * @throws IOException
     */
    public static void addFileToZip(String fileName, byte[] data,
            String password, ZipOutputStream zipOutputStream)
            throws ZipException, IOException {

        if (StringUtils.isEmpty(fileName) || data == null || data.length == 0
            || zipOutputStream == null) {
            throw new ZipException(new StringBuilder("参数异常,fileName=")
                .append(fileName).append(",data=").append(data)
                .append(",zipOutputStream=").append(zipOutputStream).toString());
        }

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别

        zipParameters.setFileNameInZip(fileName);

        if (StringUtils.isNotBlank(password)) {
            zipParameters.setEncryptFiles(true);
            zipParameters
                .setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            zipParameters.setPassword(password.toCharArray());
        }

        // 源文件是否为外部流，true表示使用内存文件而非本地存储文件
        zipParameters.setSourceExternalStream(true);

        zipOutputStream.putNextEntry(null, zipParameters);
        zipOutputStream.write(data);
        zipOutputStream.closeEntry();
    }

    /**
     * @desc 将内存文件写入zip内。注意：最后必须调用closeZipOutputStream关闭输出流，或者手动关闭
     * @param fileName
     *        文件名
     * @param data
     *        文件数据
     * @param zipOutputStream
     * @throws ZipException
     * @throws IOException
     */
    public static void addFileToZip(String fileName, byte[] data,
            ZipOutputStream zipOutputStream) throws ZipException, IOException {
        Zip4jUtils.addFileToZip(fileName, data, null, zipOutputStream);
    }

    /**
     * @desc 将内存文件写入zip内。注意：最后必须调用closeZipOutputStream关闭输出流，或者手动关闭
     * @param zipParameters
     *        zip参数
     * @param data
     *        文件数据
     * @param zipOutputStream
     *        输出流
     * @throws ZipException
     * @throws IOException
     */
    public static void addFileToZip(ZipParameters zipParameters, byte[] data,
            ZipOutputStream zipOutputStream) throws ZipException, IOException {

        if (zipParameters == null || data == null || data.length == 0
            || zipOutputStream == null) {
            throw new ZipException(new StringBuilder("参数异常,zipParameters=")
                .append(zipParameters).append(",data=").append(data)
                .append(",zipOutputStream=").append(zipOutputStream).toString());
        }
        zipOutputStream.putNextEntry(null, zipParameters);
        zipOutputStream.write(data);
        zipOutputStream.closeEntry();
    }

    /**
     * @desc 关闭流
     * @param zipOutputStream
     *        输出流
     * @throws IOException
     * @throws ZipException
     */
    public static void closeZipOutputStream(ZipOutputStream zipOutputStream)
            throws IOException, ZipException {
        if (zipOutputStream == null) {
            return;
        }
        zipOutputStream.finish();
        zipOutputStream.close();
    }

    /**
     * 将多个内存中的文件写入到一个本地ZIP文件中
     * @param sourceFile 源文件
     * @param targetFilePath 目标文件路径
     * @return 本地文件的绝对路径
     * @throws Exception
     */
    public static void addMultiFileToOneZip(Map<String, byte[]> sourceFile, String targetFilePath) throws Exception {
        File zipFile = new File(targetFilePath);
        File parentFile = zipFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        java.util.zip.ZipOutputStream zipOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new java.util.zip.ZipOutputStream(fileOutputStream);

            Set<Entry<String, byte[]>> entries = sourceFile.entrySet();
            for (Entry<String, byte[]> entry : entries) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(fileContent, 0, fileContent.length);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(zipOutputStream != null) {
                zipOutputStream.closeEntry();
                zipOutputStream.close();
            }
            if(fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
