package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entity.Paciente;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.entity.DetalleDiente;
import com.example.demo.entity.Odontograma;
import com.example.demo.entity.Odontologo;
import com.example.demo.repository.DetalleDienteRepository;
import com.example.demo.repository.OdontogramaRepository;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.HtmlUtils;
import com.example.demo.service.OdontogramaService;
import com.example.demo.service.PdftoImageService;
import com.example.demo.service.impl.PdfServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/odontograma")
public class OdontogramaController extends HtmlUtils {

	@Autowired
	private final OdontogramaService odontogramaService;
	@Autowired
	private final PacienteRepository pacienteRepository;
	
	@Autowired
	private PdfServiceImpl pdfServiceImpl;
	@Autowired
	private PdftoImageService pdftoImageService;

	@Autowired
	private OdontologoRepository odontologoRepository;
	
	@Autowired
	private OdontogramaRepository odontogramaRepository;
	
	@Autowired
	private final DetalleDienteRepository detalleDienteRepository;
	
	@Autowired
	private SessionFactory sessionFactory;

	// Constructor para inyección de dependencias
	public OdontogramaController(OdontogramaService odontogramaService, PacienteRepository pacienteRepository,
			OdontologoRepository odontologoRepository, DetalleDienteRepository detalleDienteRepository) {
		this.odontogramaService = odontogramaService;
		this.pacienteRepository = pacienteRepository;
		this.odontologoRepository = odontologoRepository;
		this.detalleDienteRepository = detalleDienteRepository;
	}

	@PostMapping("/crear")
	public String crearOdontograma(HttpServletResponse response,
			@RequestParam("dientesEvaluados") String[] dientesEvaluados,
			@RequestParam("comentariosGenerales") String comentariosGenerales, @RequestParam("paciente") int pacienteId,
			@RequestParam("odontologo") int odontologoId, @RequestParam Map<String, String> notasDientesParams,
			@RequestParam("botonesVisibles[]") List<String> botonesVisibles) throws IOException {
		 odontogramaService.crearOdontograma(dientesEvaluados, comentariosGenerales,
		 pacienteId, odontologoId, notasDientesParams, botonesVisibles);
		 return "redirect:/odontograma/odontogramas";
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
	public String obtenerOdontogramas(Model modelo) {
	    List<Odontograma> odontogramas = (ArrayList<Odontograma>) odontogramaRepository.findAll();
	    modelo.addAttribute("odontogramas",odontogramas);
	    return "odontograma/listadoOdontogramas";
	}

	@GetMapping("/{id}/imagen")
	@Transactional
	public void descargarImagenOdontograma(@PathVariable Long id, HttpServletResponse response) throws IOException {
	    Odontograma odontograma;
	    Session session = null;
	    try {
	        session = sessionFactory.openSession();
	        odontograma = odontogramaRepository.getById(id);
	        byte[] imageBytes = odontograma.getImagenOdontograma();
	        String fileName = "odontograma_" + id + ".jpeg";
	        pdftoImageService.downloadImage(response, imageBytes, fileName);
	    } finally {
	        if (session != null) {
	            session.close();
	        }
	    }
	}

	@GetMapping("/{id}/pdf")
	@Transactional
	public void descargarPdfOdontograma(@PathVariable Long id, HttpServletResponse response) throws IOException {
	    Odontograma odontograma;
	    Session session = null;
	    try {
	        session = sessionFactory.openSession();
	        odontograma = odontogramaRepository.getById(id);
	        byte[] pdfBytes = odontograma.getPdfOdontograma();
	        String fileName = "odontograma_" + id + ".pdf";
	        pdfServiceImpl.descargarPdf(response, pdfBytes, fileName);
	    } finally {
	        if (session != null && session.isOpen()) {
	            session.close();
	        }
	    }
	}
	
	@GetMapping("/delete/{id}")
	public String mostrarConfirmacionEliminarOdontograma(@PathVariable Long id, Model model) {
	    Odontograma odontograma = odontogramaRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Odontograma no encontrado con el ID: " + id));

	    model.addAttribute("odontograma", odontograma);
	    return "odontograma/confirmarEliminarOdontograma";
	}
	
	@PostMapping("/delete/{id}")
	public String eliminarOdontograma(@PathVariable Long id) {
	    odontogramaService.eliminarOdontograma(id);
	    return "redirect:/odontograma/odontogramas";
	}
	
	@GetMapping("/update/{id}")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
	    Odontograma odontograma = odontogramaRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Odontograma no encontrado con el ID: " + id));

	    List<DetalleDiente> detallesDientes = detalleDienteRepository.findByOdontograma(odontograma);

	    List<String> dientesEvaluados = new ArrayList<>();
	    for (DetalleDiente detalleDiente : detallesDientes) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(detalleDiente.getPosicionDiente());
	        sb.append(" - ");
	        sb.append(detalleDiente.getEstado());
	        if (detalleDiente.getNota() != null && !detalleDiente.getNota().isEmpty()) {
	            sb.append(" - Nota: ");
	            sb.append(detalleDiente.getNota());
	        }
	        dientesEvaluados.add(sb.toString());
	    }

	    model.addAttribute("odontograma", odontograma);
	    model.addAttribute("pacientes", pacienteRepository.findAll());
	    model.addAttribute("odontologos", odontologoRepository.findAll());
	    model.addAttribute("botones", generateButtons());
	    model.addAttribute("estadosDentales", Odontograma.listaEstadosDentales);
	    model.addAttribute("dientesEvaluados", dientesEvaluados);

	    return "odontograma/formularioEditarOdontograma";
	}

	@PostMapping("/update/{id}")
	public String actualizarOdontograma(@PathVariable Long id,
	                                     @RequestParam("dientesEvaluados") String[] dientesEvaluados,
	                                     @RequestParam("comentariosGenerales") String comentariosGenerales,
	                                     @RequestParam("paciente") int pacienteId,
	                                     @RequestParam("odontologo") int odontologoId,
	                                     @RequestParam Map<String, String> notasDientesParams,
	                                     @RequestParam("botonesVisibles[]") List<String> botonesVisibles)
	        throws IOException {

	    Odontograma odontograma = odontogramaRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Odontograma no encontrado con el ID: " + id));

	    odontogramaService.actualizarOdontograma(odontograma, dientesEvaluados, comentariosGenerales, pacienteId,
	            odontologoId, notasDientesParams, botonesVisibles);

	    return "redirect:/odontograma/odontogramas";
	}
	
}
