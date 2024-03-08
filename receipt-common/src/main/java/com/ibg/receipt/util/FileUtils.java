package com.ibg.receipt.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.base.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath
     *            文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file);
                    ByteArrayOutputStream out = new ByteArrayOutputStream(2048)) {
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                data = out.toByteArray();
            }
        }
        return data;
    }

    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    public static void byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] unZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    public static void unZipFiles(String zipfile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipfile);
        for (Enumeration entries = zip.getEntries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");

            // 获取当前file的父路径,这才是文件夹
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));

            // 判断路径是否存在,不存在则创建文件路径
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }

    }

    /**
     * 保存文件
     */
    public static String saveWorkbook(Workbook workBook, String filePath) throws ServiceException {
        try {
            File file = new File(filePath);
            File partenFile = file.getParentFile();
            // 如果文件夹不存在则创建
            if (!partenFile.exists()) {
                partenFile.mkdirs();
            }
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath))) {
                workBook.write(os);
                return filePath;
            }
        } catch (IOException e) {
            log.error("输出流close失败IOException {}", e);
            throw ExceptionUtils.commonError("文件保存失败");
        }
    }
    
    public static void deleteQuietly(String filePath) {
        try {
            deleteQuietly(new File(filePath));
        }catch (Exception e){
            log.error("删除文件错误");
        }
    }

    /**
     * 保存文本文件
     */
    public static void saveFileData(List<String> lines, String filePath) {
        try {
            File file = new File(filePath);
            File partenFile = file.getParentFile();
            // 如果文件夹不存在则创建
            if (!partenFile.exists()) {
                partenFile.mkdirs();
            }
            FileUtils.writeLines(new File(filePath), StandardCharsets.UTF_8.toString(), lines);
        } catch (IOException e) {
            String message = String.format("写入文本文件失败,message:%s", e);
            log.error(message);
            throw ExceptionUtils.commonError(message);
        }
    }

    public static void main(String[] args) throws Exception {

        URL url= new URL("http://172.16.2.122:8788/v1/file/download/NJJRvi");
        File file = new File(url.toURI());

        System.out.println(file.getAbsolutePath());

        unZipFiles("/home/ning/rrd/test/PartnerId_20211130_Statement.zip", "/home/ning/rrd/test/PartnerId_20211130_Statement/");
    }

    /**
     * @desc 多文件打包为.gz
     * @author xudong
     * @date 2020/10/17
     */
    public static File packFiles(List<File> sources, File target) throws IOException {
        try (FileOutputStream out = new FileOutputStream(target);
                TarArchiveOutputStream os = new TarArchiveOutputStream(out)) {
            for (File file : sources) {
                os.putArchiveEntry(new TarArchiveEntry(file, file.getName()));
                IOUtils.copy(new FileInputStream(file), os);
                os.closeArchiveEntry();
            }
        } catch (Exception e) {
            log.error("多文件打包为.gz失败,targetFileName:", target.getName());
            throw ExceptionUtils.commonError("多文件打包为.gz失败");
        }
        return target;
    }



    public static byte[] getByteArrayContent(String filePath) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(filePath);
            conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            return bos.toByteArray();
        }catch (Exception e){
            log.error("从url:[]获取文件流失败", filePath, e);
        }finally {
            try {
                if(bos !=null) bos.close();
            }catch (Exception e){
                log.error("通过url下载文件close异常！",e);
            }
            try {
                if(conn !=null) conn.disconnect();
            }catch (Exception e){
                log.error("通过url下载文件close异常！",e);
            }
        }
        return null;
    }
}
