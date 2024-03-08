package com.ibg.receipt.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

public class ImageConverter {

    public static byte[] convertFormat(byte[] input, String toFormat)
            throws IOException {

        try (InputStream is = new ByteArrayInputStream(input);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            BufferedImage inputImage = ImageIO.read(is);
            ImageIO.write(inputImage, toFormat, bos);
            return bos.toByteArray();
        }
    }
    
    
    /**
     * 缩放图片
     *
     * @param source
     *            - 图片流
     * @param scale
     *            - 缩放比例
     * @return
     * @throws IOException
     */
    public static byte[] scaleImage(byte[] source, double scale) throws IOException {
        try (InputStream is = new ByteArrayInputStream(source);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            BufferedImage bi = ImageIO.read(is);
            Thumbnails.of(bi).scale(scale).outputFormat("png").toOutputStream(bos);
            return bos.toByteArray();
        }
    }

    /**
     *
     * 压缩图片
     *
     * @param source
     * @param maxSizeKb
     * @return
     * @throws IOException
     */
    public static byte[] compressImage(byte[] source, int maxSizeKb) throws IOException {
        int size = source.length / 1024;
        float quality = 0.9f;
        byte[] compressed = null;
        while (size > maxSizeKb) {
            try (InputStream is = new ByteArrayInputStream(source);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                BufferedImage bi = ImageIO.read(is);
                Thumbnails.of(bi).scale(1f).outputQuality(quality).outputFormat("jpg").toOutputStream(bos);
                compressed = bos.toByteArray();
                size = compressed.length / 1024;
                quality = quality - 0.05f;
            }
        }
        return compressed;
    }
}
