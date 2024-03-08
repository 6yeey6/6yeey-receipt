package com.ibg.receipt.util;

import com.ibg.receipt.base.exception.ServiceException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件压缩
 *
 * @author TK
 * @since 2021-04-26
 */
public class ZipOutputStreamUtil {
    private static final Logger log = LoggerFactory.getLogger(ZipOutputStreamUtil.class);

    public static void main(String[] args) {

        String outPath = "C:\\Users\\liuye07\\Desktop\\test.zip";

        List<File> filePath = new ArrayList<>();//压缩文件路径
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\1.xls"));
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\2.xls"));
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\3.xls"));

        String password = "222222";//加密密码

        zipFilesAndEncrypt(filePath, outPath, password);
    }

    /**
     * 文件压缩
     *
     * @param filePath 被压缩文件集合
     * @param outPath  输出地址
     * @param password 密码
     * @return
     */
    public static ZipFile zipFilesAndEncrypt(List<File> filePath, String outPath, String password) {

        log.info("开始压缩文件...........");

        long old = System.currentTimeMillis();

        boolean result = false;

        ArrayList<File> filesToAdd = new ArrayList<>();//压缩路径的集合

        for (int i = 0; i < filePath.size(); i++) {//遍历压缩文件数据

            filesToAdd.add(filePath.get(i));
        }

        //压缩配置
        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);//压缩方式
            //设置压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);//压缩级别
            if (password != null && password != "") {
                parameters.setEncryptFiles(true);//设置压缩文件加密
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);//加密方式
                parameters.setPassword(password);//设置加密密码
            }
            ZipFile zipFile = new ZipFile(outPath);//创建压缩路径
            zipFile.setFileNameCharset("gbk");//设置压缩编码
            zipFile.addFiles(filesToAdd, parameters);//添加压缩文件并进行加密压缩
            return zipFile;
        } catch (ZipException e) {
            log.error("文件压缩出错", e);
            throw new ServiceException("文件压缩出错:"+e);
        }
        //long now = System.currentTimeMillis();
        //log.info("压缩成功，共耗时：" + ((now - old) / 1000.0) + "秒........"); // 转化用时
        //return zipFile;
    }

}