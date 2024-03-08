package com.ibg.receipt.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfToJpg {
    
    public static void pdfToJpg(String dir, String jpgName, byte[] pdfContent) {
        try (final PDDocument document = PDDocument.load(pdfContent)){
            PDFRenderer pdfRenderer = new PDFRenderer(document);
                BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
                String fileName = dir + "/" + jpgName + ".jpg";
                ImageIOUtil.writeImage(bim, fileName, 300);
        } catch (IOException e) {
            log.error("pdf to jpg error", e);
        }
    }
}
