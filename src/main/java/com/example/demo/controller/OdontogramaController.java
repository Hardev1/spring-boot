package com.example.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Paciente;
import com.example.demo.entity.Odontograma;
import com.example.demo.entity.Odontologo;
import com.example.demo.repository.OdontogramaRepository;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.services.HtmlServiceImpl;
import com.example.demo.services.HtmlUtils;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/odontograma")
public class OdontogramaController extends HtmlUtils {

	@Autowired
	private HtmlServiceImpl htmlServiceImpl;

	@Autowired
	private final PacienteRepository pacienteRepository;

	@Autowired
	private OdontologoRepository odontologoRepository;

	@Autowired
	private OdontogramaRepository odontogramaRepository;

	// Constructor para inyección de dependencias
	public OdontogramaController(PacienteRepository pacienteRepository, OdontologoRepository odontologoRepository) {
		this.pacienteRepository = pacienteRepository;
		this.odontologoRepository = odontologoRepository;
	}

	@PostMapping("/crear")
	public String crearOdontograma(@RequestParam("dientesEvaluados") String[] dientesEvaluados,
			@RequestParam("paciente") int pacienteId, @RequestParam("odontologo") int odontologoId,
			@RequestParam Map<String, String> params) throws IOException {

		// Obtener el paciente y el odontólogo de la base de datos
		Paciente paciente = pacienteRepository.findById(pacienteId)
				.orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
		Odontologo odontologo = odontologoRepository.findById(odontologoId)
				.orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

		// Crear una nueva instancia de Odontograma y establecer los valores
		Odontograma odontograma = new Odontograma();
		odontograma.setPaciente(paciente);
		odontograma.setOdontologo(odontologo);
		odontograma.setFechaCreacion(LocalDateTime.now());
		odontograma.setUltimaModificacion(LocalDateTime.now());
		odontograma.setGuardadoAutomatico(false);

		// Convertir la lista de dientes evaluados en un mapa de estados y un mapa de
		// notas
		Map<String, String> estadosDentales = new HashMap<>();
		Map<String, String> notasDentales = new HashMap<>();

		// Recorrer la lista de dientes evaluados para extraer el número de diente, el
		// estado y la nota
		for (String diente : dientesEvaluados) {
			// Separar el número de diente y el estado dental
			String[] partes = diente.split(" - ");
			String[] numerodiente = partes[0].split(" ");
			String numeroDienteString = numerodiente[2]; // Obtener el número de diente

			String estadoDental = partes[1]; // Obtener el estado dental
			// Agregar el estado dental al mapa
			estadosDentales.put(numeroDienteString, estadoDental);

			// Obtener la nota del diente
			String notaDiente = params.get("notaDiente_" + numeroDienteString);
			System.out.println("Nota del diente " + numeroDienteString + ": " + notaDiente);
			notasDentales.put(numeroDienteString, notaDiente);
		}

		// Establecer los estados y notas dentales en el odontograma
		odontograma.setEstadosDentales(estadosDentales);
		odontograma.setNotasDentales(notasDentales);

		// Generar el PDF y obtener su contenido en formato de bytes
		byte[] pdfBytes = htmlServiceImpl.generarPdf(Arrays.asList(dientesEvaluados), paciente, odontologo,
				LocalDateTime.now(), "");

		// Guardar el PDF en la entidad
		odontograma.setPdfOdontograma(pdfBytes);

		// Guardar el Odontograma en la base de datos
		odontogramaRepository.save(odontograma);

		return "odontograma/formularioNuevoOdontograma";
	}

//byte[] pdfBytes = htmlServiceImpl.generarPdf(Arrays.asList(dientesEvaluados), paciente, odontologo, LocalDateTime.now(), "");

	private Map<String, String> convertirAHashMap(String[] valores) {
		Map<String, String> mapa = new HashMap<>();
		if (valores != null && valores.length > 0) {
			for (String par : valores) {
				// Dividir cada par en clave y valor separados por "-"
				String[] partes = par.trim().split(" - ");
				if (partes.length == 2) {
					// Eliminar espacios en blanco y almacenar en el mapa
					mapa.put(partes[0].trim(), partes[1].trim());
				}
			}
		}
		return mapa;
	}

	private int obtenerNumeroDiente(String diente) {
		// Separamos la cadena por espacios para obtener los elementos individuales
		String[] partes = diente.split(" ");

		// El número de diente está en la tercera posición del array
		// Removemos los espacios en blanco alrededor del número antes de convertirlo a
		// entero
		String numeroDienteStr = partes[2].trim();

		// Convertimos el número de diente a entero y lo retornamos
		return Integer.parseInt(numeroDienteStr);
	}

	@GetMapping("/ver")
	public String verProcesoOdontograma(Model model) {
		List<Paciente> pacientes = pacienteRepository.findAll();
		List<Odontologo> odontologos = odontologoRepository.findAll();
		List<String> botones = generateButtons();

		model.addAttribute("pacientes", pacientes);
		model.addAttribute("odontologos", odontologos);
		model.addAttribute("botones", botones);
		model.addAttribute("estadosDentales", Odontograma.listaEstadosDentales);
		return "odontograma/formularioNuevoOdontograma";
	}

	@PostMapping("/procesar-boton")
	@ResponseBody // Esto indica que el método devuelve el cuerpo de la respuesta HTTP
	public ResponseEntity<String> procesarBotonSeleccionado(@RequestParam("botonSeleccionado") String botonSeleccionado,
			Model model) {
		boolean mostrarEstadoDental = true;
		model.addAttribute("mostrarEstadoDental", mostrarEstadoDental);
		return ResponseEntity.ok("Valor del botón procesado correctamente: " + botonSeleccionado);
	}
}
