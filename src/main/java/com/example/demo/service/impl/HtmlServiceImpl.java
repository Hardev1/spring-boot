package com.example.demo.service.impl;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;
import com.example.demo.service.HtmlUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class HtmlServiceImpl extends HtmlUtils {

	private static final String DATE_PATTERN = "d 'de' MMMM 'del' yyyy 'a las' h:mm a";
	private static final Locale LOCALE = new Locale("es", "ES");
	private static final ZoneId ZONE_ID = ZoneId.systemDefault();

	public String generateHtmlContent(String[] dientesEvaluados, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String notas, List<String> botonesVisibles, List<String> notasDientes) {
		try {
			List<String> dientesEvaluadosList = Arrays.asList(dientesEvaluados);
			List<String> nombresImagenes = extraerNombresImagenes(dientesEvaluadosList);
			List<String> posicionImagenes = extraerPosicionImagenes(dientesEvaluadosList);

			List<String> imagenesIniciales = initializeImages();
			List<String> posicionesIniciales = generarPosicionesIniciales();

			setDefaultImages(botonesVisibles, imagenesIniciales, posicionesIniciales);
			replaceImages(nombresImagenes, posicionImagenes, imagenesIniciales, posicionesIniciales);

			String htmlContent = readHtmlContentFromFile("classpath:templates/odontograma/verOdontograma.html");
			String imagenesHtml = generateImagesHtml(imagenesIniciales, posicionesIniciales);

			htmlContent = reemplazarImagenesEnHtml(htmlContent, imagenesHtml);
			htmlContent = agregarBootstrapCss(htmlContent);
			htmlContent = addPatientInfoToHtml(htmlContent, paciente, odontologo, fechaEdicion, notas,
					botonesVisibles.size(), notasDientes);

			return htmlContent;

		} catch (IOException e) {
			logError(e);
			return "Error al cargar el archivo HTML";
		}
	}

	private List<String> initializeImages() {
		return new ArrayList<>(Collections.nCopies(32, ""));
	}

	private void setDefaultImages(List<String> botonesVisibles, List<String> imagenesIniciales,
			List<String> posicionesIniciales) {
		for (int i = 0; i < posicionesIniciales.size(); i++) {
			String posicion = posicionesIniciales.get(i);
			if (botonesVisibles.contains(posicion)) {
				imagenesIniciales.set(i, "estado_indefinido.png");
			}
		}
	}

	private void replaceImages(List<String> nombresImagenes, List<String> posicionImagenes,
			List<String> imagenesIniciales, List<String> posicionesIniciales) {
		for (int i = 0; i < nombresImagenes.size(); i++) {
			int indiceImagen = posicionesIniciales.indexOf(posicionImagenes.get(i));
			if (indiceImagen != -1) {
				imagenesIniciales.set(indiceImagen, nombresImagenes.get(i));
			}
		}
	}

	private String generateImagesHtml(List<String> imagenesIniciales, List<String> posicionesIniciales) {
		StringBuilder imagenesHtml = new StringBuilder();
		for (int i = 0; i < imagenesIniciales.size(); i++) {
			String imagen = imagenesIniciales.get(i);
			String posicion = posicionesIniciales.get(i);
			int distanciaX = calcularDistanciaHorizontal(posicion);
			int distanciaY = calcularDistanciaVertical(posicion);
			imagenesHtml.append(generarImagenHtml(imagen, distanciaX, distanciaY));
		}
		return imagenesHtml.toString();
	}

	private void logError(IOException e) {
// Log the error using a logging framework (e.g. Log4j, Logback)
// e.printStackTrace(); // Remove this line
	}

	public List<String> extraerNombresImagenes(List<String> listaDientesEvaluados) {
		List<String> nombresImagenes = new ArrayList<>();
		for (String diente : listaDientesEvaluados) {
			String[] partes = diente.trim().split("-");
			if (partes.length == 3) {
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
			if (partes.length == 3) {
				String nombreImagen = partes[0].trim();
				nombresImagenes.add(nombreImagen);
			}
		}
		return nombresImagenes;
	}

	public String addPatientInfoToHtml(String htmlContent, Paciente paciente, Odontologo odontologo,
			LocalDateTime fechaEdicion, String comentarioGeneral, int longitudBotonesVisibles,
			List<String> notasDientes) {
		htmlContent = replacePatientInfo(htmlContent, paciente);
		htmlContent = replaceOdontologoInfo(htmlContent, odontologo);
		htmlContent = replaceFechaEdicion(htmlContent, fechaEdicion);
		htmlContent = replaceNotas(htmlContent, notasDientes);
		htmlContent = replaceComentariosGenerales(htmlContent, comentarioGeneral);
		htmlContent = adjustBorders(htmlContent, longitudBotonesVisibles);
		return htmlContent;
	}

	private String replacePatientInfo(String htmlContent, Paciente paciente) {
		String nombreCompletoPaciente = getFullName(paciente.getNombres(), paciente.getApellidos());
		htmlContent = replace(htmlContent, "paciente_nombre", nombreCompletoPaciente);
		htmlContent = replace(htmlContent, "paciente_fecha_nacimiento",
				paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento().toString() : "");
		htmlContent = replace(htmlContent, "paciente_telefono",
				paciente.getTelefono() != null ? paciente.getTelefono() : "");
		htmlContent = replace(htmlContent, "paciente_direccion",
				paciente.getDireccion() != null ? paciente.getDireccion() : "");
		htmlContent = replace(htmlContent, "paciente_email", paciente.getEmail() != null ? paciente.getEmail() : "");
		htmlContent = replace(htmlContent, "paciente_cedula", paciente.getCedula() != null ? paciente.getCedula() : "");
		return htmlContent;
	}

	private String replaceOdontologoInfo(String htmlContent, Odontologo odontologo) {
		String nombreCompletoOdontologo = getFullName(odontologo.getNombres(), odontologo.getApellidos());
		htmlContent = replace(htmlContent, "odontologo", nombreCompletoOdontologo);
		return htmlContent;
	}

	private String replaceFechaEdicion(String htmlContent, LocalDateTime fechaEdicion) {
		String formattedFechaEdicion = formatFechaEdicion(fechaEdicion);
		htmlContent = replace(htmlContent, "fecha_edicion", formattedFechaEdicion);
		return htmlContent;
	}

	private String replaceNotas(String htmlContent, List<String> notasDientes) {
		StringBuilder notasHtml = new StringBuilder();

		for (String nota : notasDientes) {
			notasHtml.append("<p>").append(nota).append("</p>");
		}

		htmlContent = htmlContent.replace("<!-- notas_adicionales -->", notasHtml.toString());

		return htmlContent;
	}

	private String replaceComentariosGenerales(String htmlContent, String comentariosGenerales) {
		htmlContent = replace(htmlContent, "comentarios_generales", comentariosGenerales);
		return htmlContent;
	}

	private String adjustBorders(String htmlContent, int longitudBotonesVisibles) {
		if (longitudBotonesVisibles == 20) {
			htmlContent = htmlContent.replace("borde_superior.png", "borde_superior_pediatrico.png");
			htmlContent = htmlContent.replace("borde_inferior.png", "borde_inferior_pediatrico.png");
			htmlContent = htmlContent.replace("width: 640px;", "width: 400px; margin-left: 120px;");
		}
		return htmlContent;
	}

	private String getFullName(String nombres, String apellidos) {
		return (nombres != null ? nombres : "") + (apellidos != null ? " " + apellidos : "");
	}

	private String formatFechaEdicion(LocalDateTime fechaEdicion) {
		if (fechaEdicion == null) {
			return "";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, LOCALE);
		String hourPart = fechaEdicion.getHour() == 1 ? "a la" : "a las";
		formatter = formatter.withZone(ZONE_ID);
		return fechaEdicion.format(formatter).replace("a las", hourPart);
	}

	private String replace(String htmlContent, String placeholder, String value) {
		return htmlContent.replace("<!-- " + placeholder + " -->", value);
	}

}
