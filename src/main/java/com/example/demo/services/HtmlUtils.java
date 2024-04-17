package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlUtils {

	@Autowired
	private ResourceLoader resourceLoader;

	public List<String> generarPosicionesIniciales() {
		List<String> botones = generateButtons();
		List<String> posicionesIniciales = new ArrayList<>();

		String[] lados = { "Izquierda", "Derecha" };
		String[] ubicaciones = { "superior", "inferior" };

		for (String lado : lados) {
			for (String ubicacion : ubicaciones) {
				for (int i = 0; i < 8; i++) {
					String numero = botones
							.get(i + (lado.equals("Derecha") ? 8 : 0) + (ubicacion.equals("inferior") ? 16 : 0));
					String posicion = lado + " " + ubicacion + " " + numero;
					posicionesIniciales.add(posicion);
				}
			}
		}

		return posicionesIniciales;
	}

	public String readHtmlContentFromFile(String filePath) throws IOException {
		Resource resource = resourceLoader.getResource(filePath);
		return new String(Files.readAllBytes(Paths.get(resource.getURI())));
	}

	public String generarImagenesHtml(List<String> imagenesIniciales) {
		StringBuilder imagenesHtml = new StringBuilder();
		for (int i = 0; i < imagenesIniciales.size(); i++) {
			String imagen = imagenesIniciales.get(i);
			int distanciaX = calcularDistanciaHorizontal(imagen);
			int distanciaY = calcularDistanciaVertical(imagen);
			imagenesHtml.append(generarImagenHtml(imagen, distanciaX, distanciaY));
		}
		return imagenesHtml.toString();
	}

	public String generarImagenHtml(String imagen, int distanciaX, int distanciaY) {
		return String.format(
				"<img class=\"img-fluid position-absolute\" style=\"width: 40px; height: 40px; left: %dpx; top: %dpx;\" src=\"classpath:/static/img/%s\" />",
				distanciaX, distanciaY, imagen);
	}

	public String reemplazarImagenesEnHtml(String htmlContent, String imagenesHtml) {
		return htmlContent.replace("<!-- insertar imagenes desde metodo de springboot -->", imagenesHtml);
	}

	public String agregarBootstrapCss(String htmlContent) {
		String bootstrapCss = "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\"/>";
		return htmlContent.replace("</head>", bootstrapCss + "</head>");
	}

	public int calcularDistanciaHorizontal(String posicion) {
		String[] partes = posicion.split(" ");
		int distanciaHorizontal = 0;
		if (partes.length == 3) {
			String lado = partes[0].toLowerCase();
			int numero = Integer.parseInt(partes[2]);
			if (lado.equals("izquierda")) {
				distanciaHorizontal = 280 - (numero - 1) * 40;
			} else if (lado.equals("derecha")) {
				distanciaHorizontal = 320 + (numero - 1) * 40;
			}
		}
		return distanciaHorizontal;
	}

	public int calcularDistanciaVertical(String posicion) {
		String[] partes = posicion.split(" ");
		int distanciaVertical = 0;
		if (partes.length == 3) {
			String ubicacion = partes[1].toLowerCase();
			if (ubicacion.contains("inferior")) {
				distanciaVertical = 40;
			}
		}
		return distanciaVertical;
	}

	public List<String> generateButtons() {
		List<String> botones = new ArrayList<>();
		int num = 8;
		boolean incrementar = false;

		for (int i = 1; i < 30; i++) {
			botones.add("" + num);

			if (num == 1 || (num == 8 && i != 1)) {
				botones.add("" + num);
				incrementar = !incrementar;
			}

			num = incrementar ? num + 1 : num - 1;
		}
		return botones;
	}
}
