package com.example.demo.controller;

import com.example.demo.entity.Cita;
import com.example.demo.entity.Paciente;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo(Model model) {
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("cita", new Cita());
        return "cita/formularioNuevaCita";
    }

    @GetMapping("/find")
    public @ResponseBody Cita buscarId(@RequestParam int id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con el ID: " + id));
    }

    @GetMapping("/all_frontend")
    public String listarTodos(Model model) {
        List<Cita> citas = citaRepository.findAll();
        model.addAttribute("citas", citas);
        return "cita/listadoCitas";
    }

    @GetMapping("/all")
    public @ResponseBody Iterable<Cita> listarTodos() {
        return citaRepository.findAll();
    }

    @PostMapping("/new_frontend")
    public String nueva(@RequestParam int pacienteId,
                        @RequestParam LocalDateTime fechaHora,
                        @RequestParam String motivoConsulta,
                        Model model) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + pacienteId));

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setFechaHora(fechaHora);
        cita.setMotivoConsulta(motivoConsulta);

        citaRepository.save(cita);

        model.addAttribute("mensaje", "Cita creada correctamente");

        return "redirect:/cita/all_frontend";
    }

    @CrossOrigin
    @GetMapping(path = "/update/{id}")
    public String editarViewCita(Model model, @PathVariable("id") int id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con ID:" + id));
        model.addAttribute("cita", cita);
        model.addAttribute("fechaHoraFormateada", cita.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        return "cita/formularioActualizarCita";
    }

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

    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminarCita(@PathVariable int id, Model model) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con el ID: " + id));
        model.addAttribute("cita", cita);
        return "cita/confirmarEliminarCita";
    }

    @PostMapping("/delete/{id}")
    public String eliminarCita(@PathVariable int id) {
        citaRepository.deleteById(id);
        return "redirect:/cita/all_frontend";
    }
}
