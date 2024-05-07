package com.example.demo.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.InputStream;

@Service
public class PdftoImageService {

    public byte[] convertPdfToImage(byte[] pdfBytes) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
            PDDocument document = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(document);
            int numPages = document.getNumberOfPages();
            List<BufferedImage> images = new ArrayList<>(numPages);
            for (int page = 0; page < numPages; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, 300);
                images.add(image);
            }
            document.close();

            // Combine the images into a single multi-page image
            int width = images.get(0).getWidth();
            int height = images.stream().mapToInt(BufferedImage::getHeight).sum();
            BufferedImage multiPageImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = multiPageImage.createGraphics();
            int y = 0;
            for (BufferedImage image : images) {
                graphics.drawImage(image, 0, y, null);
                y += image.getHeight();
            }
            graphics.dispose();

            // Convert the multi-page image to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(multiPageImage, "jpeg", outputStream);
            return outputStream.toByteArray();
        }
    }

    public void downloadImage(HttpServletResponse response, byte[] imageBytes, String fileName) throws IOException {
        // Set the response headers to prompt the user to download the image
        response.setContentType("image/jpeg"); // Set the content type to JPEG
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".jpeg"); // Set the file name and extension
        response.setContentLength(imageBytes.length); // Set the content length

        // Write the image bytes to the response output stream
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(imageBytes);
        outputStream.flush();
        outputStream.close();
    }
}