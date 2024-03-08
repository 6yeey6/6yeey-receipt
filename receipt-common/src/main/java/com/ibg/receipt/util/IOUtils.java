package com.ibg.receipt.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2020/2/11 15:53 <br>
 * @description IOUtils <br>
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    public static void writeLinesWithoutLastLineSeparator(final Collection<?> lines, String lineEnding, final OutputStream output,
                                                          final Charset encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        org.apache.commons.io.IOUtils.writeLines(lines, lineEnding, baos, encoding);
        //删除最后一个换行符
        String content = new String(baos.toByteArray(),encoding);
        if(content.length()>0){
            content = content.substring(0,content.length()- lineEnding.length());
        }
        baos.close();
        output.write(content.getBytes(encoding));
    }
}
