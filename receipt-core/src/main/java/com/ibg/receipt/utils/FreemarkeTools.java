package com.ibg.receipt.utils;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
public class FreemarkeTools {


    /**
     * 获取模板转为html
     */
    public static String ftlToString(Map<String,Object> map, String templateName) throws IOException, TemplateException {
        String value = "";
        Configuration configuration = new Configuration();
        Resource resource = new ClassPathResource("templates");
        File sourceFile =  resource.getFile();
        String ftlPath = sourceFile.getAbsolutePath();
        String filName = templateName;
        String encoding = "UTF-8";

        StringWriter out = new StringWriter();
        configuration.setDirectoryForTemplateLoading(new File(ftlPath));
        Template template = configuration.getTemplate(filName,encoding);
        template.setEncoding(encoding);

        template.process(map, out);
        out.flush();
        out.close();
        value = out.getBuffer().toString();
        return value;
    }

    /**
     * html转为图片
     * @param html
     * @param inputFileName
     * @param outputFileName
     * @param widthImage
     * @param heightImage
     * @return
     * @throws IOException
     */
    public static String turnImage(String html, String inputFileName, String outputFileName
            ,int widthImage, int heightImage) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFileName),"UTF-8"));
        bufferedWriter.write(html);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();

        File f = new File(inputFileName);
        Java2DRenderer renderer = new Java2DRenderer(f, widthImage, heightImage);
        BufferedImage image = renderer.getImage();
        FSImageWriter imageWriter = new FSImageWriter();
        imageWriter.setWriteCompressionQuality(0.9f);
        File imgFile = new File(outputFileName);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(imgFile);
            imageWriter.write(image, fout);
        } finally {
            if(fout != null) {
                fout.close();
            }
        }

        return outputFileName;
    }
}
