package com.ibg.receipt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2021/3/11 19:10 <br>
 * @description TarUtil <br>
 */
public class TarUtil {
    /**
     * 多文件压缩成tar文件
     *
     * @param fileMap
     * @return
     */
    public static byte[] compressTarFiles(Map<String, byte[]> fileMap) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TarArchiveOutputStream tos = new TarArchiveOutputStream(byteArrayOutputStream)) {
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(entry.getKey());
                tarArchiveEntry.setSize(entry.getValue().length);
                tos.putArchiveEntry(tarArchiveEntry);
                tos.write(entry.getValue());
                tos.closeArchiveEntry();
            }
            tos.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "文件压缩异常:" + e.getMessage());
        }
    }

    /**
     * tar文件解压成多文件
     *
     * @param tarBytes
     * @return
     * @throws IOException
     */
    public static Map<String, byte[]> decompressTarFiles(byte[] tarBytes) throws IOException {
        Map<String, byte[]> entryMap = new HashMap<>();
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes))) {
            TarArchiveEntry tarArchiveEntry = null;
            while ((tarArchiveEntry = (TarArchiveEntry) tis.getNextEntry()) != null) {
                if (!tis.canReadEntryData(tarArchiveEntry) || !tarArchiveEntry.isFile()) {
                    continue;
                }
                String fileName = tarArchiveEntry.getName();
                int size = (int) tarArchiveEntry.getSize();
                byte[] buf = new byte[size];
                tis.read(buf, 0, size);
                entryMap.put(fileName, buf);
            }
        } catch (Exception e) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "文件解压缩异常:" + e.getMessage());
        }
        return entryMap;
    }

    public static void main(String[] args) {
        System.out.println("args = " + Arrays.deepToString(args));
        try (FileInputStream fi = new FileInputStream(new File("C:\\Users\\chenhao18\\Desktop\\测试数据\\IM-CRDT-20210701104345.tar"));
            ByteArrayOutputStream bo = new ByteArrayOutputStream()) {
            IOUtils.copy(fi, bo);
            Map<String,byte[]> a = decompressTarFiles(bo.toByteArray());
            for (Map.Entry<String,byte[]> entry:a.entrySet()){
                File file = new File("E://aa//"+entry.getKey());
                FileOutputStream fo =new FileOutputStream(file);
                fo.write(entry.getValue());
                fo.close();

            }
            System.out.println("args = " + Arrays.deepToString(args));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
