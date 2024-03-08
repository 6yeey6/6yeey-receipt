package com.ibg.receipt.util;

import com.ibg.receipt.base.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.*;
import java.util.*;

@Slf4j
public class FileCompressionApiAct {
    /**
     * 复制压缩文件路径  ps:此路径必须为空文件夹,在压缩完成后此文件夹将被清空目录
     */
    private static long time = System.currentTimeMillis();//以时间戳作为文件名,防止重命名问题

    /**
     * 压缩包路径: 路径+压缩包名称    eg: C:/Users/Administrator/Desktop/压缩测试/  +  test.zip
     */
    private static String zipPath = "C:\\Users\\liuye07\\Desktop\\yasuo.zip";

    /**
     * 可支持的压缩文件格式
     */
    private static String[] fileType = {"doc", "docx", "pdf", "txt", "xlsx", "xls", "jpg"};

    /**
     * @param filePath 压缩文件路径
     * @param password 加密密码
     * @return
     * @Title: zipFilesAndEncrypt
     * @Description: 将指定路径下的文件压缩至指定zip文件，并以指定密码加密,若密码为空，则不进行加密保护
     * @Author: 张庆裕
     * @Date: 2021/01/04
     */
    //@RequestMapping("/fileCompression/list")
    public static ZipFile zipFilesAndEncrypt(List<File> filePath, String password) {
        /**
         * 压缩成功的文件数量
         */
        int successCount = 0;
        /**
         * 压缩失败的文件数量
         */
        int failCount = 0;
        /**
         * 返回数据
         */
        ArrayList<String> failFile = new ArrayList<>();//压缩失败的文件路径

        ArrayList<String> failFilePath = new ArrayList<>();//路径错误的文件

        ArrayList<File> filesToAdd = new ArrayList<>();//压缩路径的集合

        for (int i = 0; i < filePath.size(); i++) {//遍历压缩文件数据
            File file = filePath.get(i);//获取原文件
            if (!file.exists()) {//防止文件异常,首先再次确认文件路径是否存在
                // 文件不存在
                failCount++;
                failFilePath.add(file.getPath());
                System.out.println("文件:" + file.getPath() + "  路径不存在!");
            } else {//文件存在
                //获取原文件路径
                String path = filePath.get(i).getPath();
                //获取最后一个点的位置
                int lastIndexOf = path.lastIndexOf(".");
                //获取文件后缀 eg: txt , doc , pdf ....
                String suffix = path.substring(lastIndexOf + 1);
                if (Arrays.asList(fileType).contains(suffix)) {     //判断文件格式是否合格,合格添加至压缩文件中
                    //获取原文件名称
                    File oldName = new File(file.getPath());
                    log.info("aaaaa:{}", oldName.getAbsolutePath());
                    log.info("bbbb:{}", oldName.getParent());
                    log.info("cccc:{}", oldName.getName());
                    filesToAdd.add(new File(oldName.getParent() + "/" + oldName.getName()));//将赋值出来的文件添加到压缩文件集合中
                    successCount++;//压缩成功文件数量+1
                } else {
                    failFile.add(file.getPath());
                    failCount++;//压缩失败文件数量+1
                    System.out.println("该文件压缩失败:" + file.getPath() + " 文件格式错误!");
                }
            }
        }
        ZipFile zipFile;
        //压缩配置
        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);//压缩方式
            //设置压缩级别
            //DEFLATE_LEVEL_FASTEST - 最低压缩级别，但压缩速度更高
            //DEFLATE_LEVEL_FAST - 低压缩级别，但压缩速度更高
            //DEFLATE_LEVEL_NORMAL - 压缩水平速度之间的最佳平衡
            //DEFLATE_LEVEL_MAXIMUM - 高压缩级别，但速度不佳
            //DEFLATE_LEVEL_ULTRA - 最高压缩级别但速度较低
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);//压缩级别
            if (password != null && password != "") {
                parameters.setEncryptFiles(true);//设置压缩文件加密
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);//加密方式
                parameters.setPassword(password);//设置加密密码
            }
            zipFile = new ZipFile(zipPath);//创建压缩路径
            zipFile.setFileNameCharset("gbk");//设置压缩编码
            zipFile.addFiles(filesToAdd, parameters);//添加压缩文件并进行加密压缩
            //压缩完成后清空复制的文件目录
        } catch (ZipException e) {
            //清空复制的文件目录
            System.out.println("文件压缩出错");
            e.printStackTrace();
            throw new ServiceException("文件压缩出错");
        }
        return zipFile;
    }

    /**
     * @Description: 文件复制
     * @Param: resource  原文件路径
     * @Param: target    新文件路径
     * @return:
     * @Author: 张庆裕
     * @Date: 2021/1/6
     */
    public static void copyFile(File resource, File target) throws Exception {
        // 输入流 --> 从一个目标读取数据
        // 输出流 --> 向一个目标写入数据
        long start = System.currentTimeMillis();
        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();
        long end = System.currentTimeMillis();
        System.out.println("复制文件:" + resource.getPath() + "  成功 耗时：" + (end - start) / 1000 + " s");
    }

    /**
     * @Description: 清空复制压缩文件下的内容
     * @Param: path 复制文件夹的路径
     * @return:
     * @Author: 张庆裕
     * @Date: 2021/1/6
     */
    public static boolean deleteDir(String path) {
        File file = new File(path);
        if (!file.exists()) {//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }
        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for (String name : content) {
            File temp = new File(path, name);
            if (temp.isDirectory()) {//判断是否是目录
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容
                temp.delete();//删除空目录
            } else {
                if (!temp.delete()) {//直接删除文件
                    System.err.println("Failed to delete " + name);
                }
            }
        }
        return true;
    }


    /**
     * @Description: 文件压缩测试接口
     * @Param:
     * @return:
     * @Author: 张庆裕
     * @Date: 2021/1/7
     */
    public static void main(String[] args) {
        List<File> filePath = new ArrayList<>();//压缩文件路径
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\yasuo\\1.xls"));
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\yasuo\\2.xls"));
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\yasuo\\3.xls"));
        filePath.add(new File("C:\\Users\\liuye07\\Desktop\\yasuo\\担保费"));

        List<String> fileRename = new ArrayList<>();//压缩文件重命名名称
        fileRename.add("1.xls");
        fileRename.add("2.xls");
        fileRename.add("3.xls");
        fileRename.add("aj.jpg");

        String password = "123456";//加密密码
        //请在单元测试进行测试, 或者将方法改为 static 方法
        zipFilesAndEncrypt(filePath, password);
        //System.out.println(result);
    }

}