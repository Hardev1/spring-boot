package com.example.demo.service.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;
import com.example.demo.service.PdfService;
import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PdfServiceImpl implements PdfService {
	
	@Autowired
	private HtmlServiceImpl htmlServiceImpl;

    @Override
    public ByteArrayInputStream convertHtmlToPdf(String htmlContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        try {
			renderer.createPDF(outputStream, false);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        renderer.finishPDF();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return inputStream;
    }

    @Override
	public byte[] generarPdfToByteArray(String[] dientesEvaluados, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String comentarioGeneral, List<String> botonesVisibles, List<String> notasDientes) throws IOException {
		// Generate the HTML content for the PDF
		String htmlContent = htmlServiceImpl.generateHtmlContent(dientesEvaluados, paciente, odontologo, fechaEdicion, comentarioGeneral, botonesVisibles, notasDientes);

		// Convert the HTML content to a PDF document
		ByteArrayInputStream pdfInputStream = convertHtmlToPdf(htmlContent);

		// Read the PDF byte array from the input stream
		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		IOUtils.copy(pdfInputStream, pdfOutputStream);
		byte[] pdfBytes = pdfOutputStream.toByteArray();

		return pdfBytes;
	}

	public void descargarPdf(HttpServletResponse response, byte[] pdfBytes, String fileName) throws IOException {
		// Set the response headers to prompt the user to download the PDF
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		// Write the PDF byte array to the response output stream
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(pdfBytes);
		outputStream.flush();
		outputStream.close();
	}
}