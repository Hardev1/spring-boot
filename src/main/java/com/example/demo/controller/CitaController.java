package com.example.demo.controller;

import com.example.demo.entity.Cita;
import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/cita")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository; // Inyectar el repositorio OdontologoRepository

    @ModelAttribute("usuarioAutenticado")
    public CustomUserDetails getUserDetails(Principal principal) {
        if (principal != null) {
            return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
    
    @PreAuthorize("hasRole('AUXILIAR')")
    @GetMapping("/all_frontend")
    public String listarTodos(Model model) {
        List<Cita> citas = citaRepository.findAll();
        model.addAttribute("citas", citas);
        return "cita/listadoCitas";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo(Model model) {
        List<Paciente> pacientes = pacienteRepository.findAll();
        List<Odontologo> odontologos = odontologoRepository.findAll(); // Obtener todos los odontólogos
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("odontologos", odontologos); // Agregar odontólogos al modelo
        model.addAttribute("cita", new Cita());
        return "cita/formularioNuevaCita";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @PostMapping("/new_frontend")
    public String nueva(@RequestParam int pacienteId,
                        @RequestParam LocalDateTime fechaHora,
                        @RequestParam String motivoConsulta,
                        @RequestParam int odontologoId, // Agregar parámetro para el ID del odontólogo
                        Model model) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + pacienteId));
        Odontologo odontologo = odontologoRepository.findById(odontologoId) // Obtener el odontólogo por su ID
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado con ID: " + odontologoId)); // Manejar excepción si el odontólogo no se encuentra

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setFechaHora(fechaHora);
        cita.setMotivoConsulta(motivoConsulta);
        cita.setOdontologo(odontologo); // Asignar el odontólogo a la cita

        citaRepository.save(cita);

        model.addAttribute("mensaje", "Cita creada correctamente");

        return "redirect:/cita/all_frontend";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @CrossOrigin
    @GetMapping(path = "/update/{id}")
    public String editarViewCita(Model model, @PathVariable("id") int id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con ID:" + id));
        model.addAttribute("cita", cita);
        model.addAttribute("fechaHoraFormateada", cita.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        List<Odontologo> odontologos = odontologoRepository.findAll(); 
        model.addAttribute("odontologos", odontologos); 
        return "cita/formularioActualizarCita";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @PostMapping("/update/{id}")
    public String actualizarCita(@PathVariable("id") int id, @ModelAttribute Cita cita,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        // Obtener la cita existente por su ID
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        
        // Asignar el ID a la cita recibida
        cita.setId(id);

        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar la cita");
            return "redirect:/cita/all_frontend";
        }

        // Asignar el paciente de la cita existente a la cita recibida
        cita.setPaciente(citaExistente.getPaciente());

        // Guardar la cita en la base de datos
        citaRepository.save(cita);

        // Agregar un mensaje al modelo para mostrar en la vista de confirmación
        redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada correctamente");
        return "redirect:/cita/all_frontend";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminarCita(@PathVariable int id, Model model) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con el ID: " + id));
        model.addAttribute("cita", cita);
        return "cita/confirmarEliminarCita";
    }

    @PreAuthorize("hasRole('AUXILIAR')")
    @PostMapping("/delete/{id}")
    public String eliminarCita(@PathVariable int id) {
        citaRepository.deleteById(id);
        return "redirect:/cita/all_frontend";
    }
}
