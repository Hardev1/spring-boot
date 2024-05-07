package com.example.demo.controller;

import java.io.IOException;
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
import com.example.demo.repository.DetalleDienteRepository;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.HtmlUtils;
import com.example.demo.service.OdontogramaService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/odontograma")
public class OdontogramaController extends HtmlUtils {

	@Autowired
	private final OdontogramaService odontogramaService;
	@Autowired
	private final PacienteRepository pacienteRepository;

	@Autowired
	private OdontologoRepository odontologoRepository;

	// Constructor para inyección de dependencias
	public OdontogramaController(OdontogramaService odontogramaService, PacienteRepository pacienteRepository,
			OdontologoRepository odontologoRepository, DetalleDienteRepository detalleDienteRepository) {
		this.odontogramaService = odontogramaService;
		this.pacienteRepository = pacienteRepository;
		this.odontologoRepository = odontologoRepository;
	}

	@PostMapping("/crear")
	public String crearOdontograma(HttpServletResponse response,
			@RequestParam("dientesEvaluados") String[] dientesEvaluados,
			@RequestParam("comentariosGenerales") String comentariosGenerales, @RequestParam("paciente") int pacienteId,
			@RequestParam("odontologo") int odontologoId, @RequestParam Map<String, String> notasDientesParams,
			@RequestParam("botonesVisibles[]") List<String> botonesVisibles) throws IOException {
		 odontogramaService.crearOdontograma(dientesEvaluados, comentariosGenerales,
		 pacienteId, odontologoId, notasDientesParams, botonesVisibles);
		 return "redirect:/odontograma/new_frontend";
	}

	@GetMapping("/new_frontend")
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
	
	@GetMapping("/odontogramas")
	public ResponseEntity<String> obtenerOdontogramas() {
	    List<Odontograma> odontogramas = odontogramaService.obtenerTodosLosOdontogramas();
	    return ResponseEntity.ok("ok");
	}
	
}
