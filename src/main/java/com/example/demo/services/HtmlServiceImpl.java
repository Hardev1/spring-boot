package com.example.demo.services;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class HtmlServiceImpl extends HtmlUtils {

	@Autowired
	private PdfService pdfService;

	/*public void generarPdf(HttpServletResponse response, List<String> listaDientesEvaluados, Paciente paciente,
			Odontologo odontologo, LocalDateTime fechaEdicion, String notas) throws IOException {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=file.pdf");
		String contenidoHtml = generateHtmlContent(listaDientesEvaluados, paciente, odontologo, fechaEdicion, notas);
		ByteArrayInputStream byteArrayInputStream = pdfService.convertHtmlToPdf(contenidoHtml);
		IOUtils.copy(byteArrayInputStream, response.getOutputStream());
	}*/
	
	public byte[] generarPdf(List<String> listaDientesEvaluados, Paciente paciente, Odontologo odontologo, LocalDateTime fechaEdicion, String notas) throws IOException {
	    String contenidoHtml = generateHtmlContent(listaDientesEvaluados, paciente, odontologo, fechaEdicion, notas);
	    ByteArrayInputStream byteArrayInputStream = pdfService.convertHtmlToPdf(contenidoHtml);
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    IOUtils.copy(byteArrayInputStream, byteArrayOutputStream);
	    return byteArrayOutputStream.toByteArray();
	}

	public String generateHtmlContent(List<String> listaDientesEvaluados, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String notas) {
		try {
			List<String> nombresImagenes = extraerNombresImagenes(listaDientesEvaluados);
			List<String> posicionImagenes = extraerPosicionImagenes(listaDientesEvaluados);
// Generar listas iniciales con 32 elementos de "estado_indefinido.png" y posiciones correspondientes
			List<String> imagenesIniciales = new ArrayList<>(Collections.nCopies(32, "estado_indefinido.png"));
			List<String> posicionesIniciales = generarPosicionesIniciales();

// Reemplazar las imágenes en las posiciones correspondientes
			for (int i = 0; i < nombresImagenes.size(); i++) {
				int indiceImagen = posicionesIniciales.indexOf(posicionImagenes.get(i));
				if (indiceImagen != -1) {
					imagenesIniciales.set(indiceImagen, nombresImagenes.get(i));
				}
			}

			String htmlContent = readHtmlContentFromFile("classpath:templates/odontograma/verOdontograma.html");

			StringBuilder imagenesHtml = new StringBuilder();
			for (int i = 0; i < imagenesIniciales.size(); i++) {
				String imagen = imagenesIniciales.get(i);
				String posicion = posicionesIniciales.get(i);
				int distanciaX = calcularDistanciaHorizontal(posicion);
				int distanciaY = calcularDistanciaVertical(posicion);
				imagenesHtml.append(generarImagenHtml(imagen, distanciaX, distanciaY));
			}

			htmlContent = reemplazarImagenesEnHtml(htmlContent, imagenesHtml.toString());
			htmlContent = agregarBootstrapCss(htmlContent);
			htmlContent = addPatientInfoToHtml(htmlContent, paciente, odontologo, fechaEdicion, notas);

			return htmlContent;

		} catch (IOException e) {
			e.printStackTrace();
			return "Error al cargar el archivo HTML";
		}
	}

	public String addPatientInfoToHtml(String htmlContent, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String notas) {
		String formattedFechaEdicion = "";

		if (fechaEdicion != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'del' yyyy 'a las' h:mm a",
					new Locale("es", "ES"));
			String hourPart = fechaEdicion.getHour() == 1 ? "a la" : "a las";
			formatter = formatter.withZone(fechaEdicion.atZone(ZoneId.systemDefault()).getZone());
			formattedFechaEdicion = fechaEdicion.format(formatter).replace("a las", hourPart);
		}

		String nombreCompletoPaciente = paciente.getNombres() != null ? paciente.getNombres() : "";
		nombreCompletoPaciente += paciente.getApellidos() != null ? " " + paciente.getApellidos() : "";
		htmlContent = htmlContent.replace("<!-- paciente_nombre -->", nombreCompletoPaciente);
		htmlContent = htmlContent.replace("<!-- paciente_fecha_nacimiento -->",
				paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento().toString() : "");
		htmlContent = htmlContent.replace("<!-- paciente_telefono -->",
				paciente.getTelefono() != null ? paciente.getTelefono() : "");
		htmlContent = htmlContent.replace("<!-- paciente_direccion -->",
				paciente.getDireccion() != null ? paciente.getDireccion() : "");
		htmlContent = htmlContent.replace("<!-- paciente_email -->",
				paciente.getEmail() != null ? paciente.getEmail() : "");
		htmlContent = htmlContent.replace("<!-- paciente_cedula -->",
				paciente.getCedula() != null ? paciente.getCedula() : "");

		String nombreCompletoOdontologo = odontologo.getNombres() != null ? odontologo.getNombres() : "";
        nombreCompletoOdontologo += odontologo.getApellidos() != null ? " " + odontologo.getApellidos() : "";
        
        htmlContent = htmlContent.replace("<!-- odontologo -->", nombreCompletoOdontologo);
		htmlContent = htmlContent.replace("<!-- fecha_edicion -->", formattedFechaEdicion);
		htmlContent = htmlContent.replace("<!-- notas_adicionales -->", notas);
		

		return htmlContent;
	}

	public String reemplazarImagenesEnHtml(String htmlContent, String imagenesHtml) {
		return htmlContent.replace("<!-- insertar imagenes desde metodo de springboot -->", imagenesHtml);
	}

	public List<String> extraerNombresImagenes(List<String> listaDientesEvaluados) {
		List<String> nombresImagenes = new ArrayList<>();
		for (String diente : listaDientesEvaluados) {
			String[] partes = diente.trim().split("-");
			if (partes.length == 2) {
				String nombreImagen = partes[1].trim().toLowerCase().replaceAll(" ", "_") + ".png";
				nombresImagenes.add(nombreImagen);
			}
		}
		return nombresImagenes;
	}

	public List<String> extraerPosicionImagenes(List<String> listaDientesEvaluados) {
		List<String> nombresImagenes = new ArrayList<>();
		for (String diente : listaDientesEvaluados) {
			String[] partes = diente.trim().split("-");
			if (partes.length == 2) {
				String nombreImagen = partes[0].trim();
				nombresImagenes.add(nombreImagen);
			}
		}
		return nombresImagenes;
	}

}
