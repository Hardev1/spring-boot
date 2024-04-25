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
import com.example.demo.entity.DetalleDiente;
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
	                               @RequestParam("paciente") int pacienteId,
	                               @RequestParam("odontologo") int odontologoId,
	                               @RequestParam Map<String, String> notasDientesParams,
	                               Model model) throws IOException {

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

	    // Recorrer la lista de dientes evaluados para extraer el número de diente, el estado y la nota
	    for (String diente : dientesEvaluados) {
	    	System.out.println(diente);
	        String[] partes = diente.split(" - ");
	        String numeroDienteString = partes[0]; // Obtener el número de diente
	        String estadoDental = partes[1]; // Obtener el estado dental
	        

	        String[] parteNota = diente.split(" - Nota: ");
	        String notaDiente = partes.length > 1 ? parteNota[1] : ""; // Obtener la nota del diente
	        System.out.println(notaDiente);

	        // Crear una instancia de DetalleDiente y agregarla a la lista
	        DetalleDiente detalleDiente = new DetalleDiente();
	        detalleDiente.setPosicionDiente(numeroDienteString);
	        detalleDiente.setEstado(estadoDental);
	        detalleDiente.setNota(notaDiente);
	        odontograma.getDetallesDientes().add(detalleDiente);
	    }

	    // Generar el PDF y obtener su contenido en formato de bytes
	    byte[] pdfBytes = htmlServiceImpl.generarPdf(Arrays.asList(dientesEvaluados), paciente, odontologo, LocalDateTime.now(), "");

	    // Guardar el PDF en la entidad
	    odontograma.setPdfOdontograma(pdfBytes);

	    // Guardar el Odontograma en la base de datos
	    //odontogramaRepository.save(odontograma);
	    
	    return verProcesoOdontograma(model);
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
