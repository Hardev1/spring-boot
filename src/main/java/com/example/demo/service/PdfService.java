package com.example.demo.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;

public interface PdfService {
    ByteArrayInputStream convertHtmlToPdf(String htmlContent);
	byte[] generarPdfToByteArray(String[] dientesEvaluados, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String notas, List<String> botonesVisibles, List<String> notasDientes) throws IOException;
}