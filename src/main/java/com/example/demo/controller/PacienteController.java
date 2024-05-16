package com.example.demo.controller;

import com.example.demo.entity.Cita;
import com.example.demo.entity.Paciente;
import com.example.demo.entity.Tratamiento;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.ArrayList;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;
    private final CitaRepository citaRepository;
    

    // Constructor para inyección de dependencias
    public PacienteController(PacienteRepository pacienteRepository, TratamientoRepository tratamientoRepository, CitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.citaRepository = citaRepository;
    }
    
    @ModelAttribute("usuarioAutenticado")
    public CustomUserDetails getUserDetails(Principal principal) {
        if (principal != null) {
            return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @PostMapping("/new")
    public @ResponseBody String nuevo(@RequestParam String nombres,
                                       @RequestParam String apellidos,
                                       @RequestParam String fechaNacimiento,
                                       @RequestParam String telefono,
                                       @RequestParam String direccion,
                                       @RequestParam String email) {
        Paciente paciente = new Paciente();
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        // Convertir fechaNacimiento a LocalDate y establecer en paciente
        // paciente.setFechaNacimiento(...);
        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);
        paciente.setEmail(email);

        pacienteRepository.save(paciente);
        return "Listo";
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo() {
        return "paciente/formularioNuevoPaciente";
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @PostMapping("/new_frontend")
    public String nuevo(@Valid Paciente paciente, Model model) {
        // Verificar si ya existe un paciente con el mismo ID
        if (pacienteRepository.existsById(paciente.getId())) {
            // Manejar el caso de ID duplicado aquí, por ejemplo, generando un nuevo ID automáticamente
            // O mostrar un mensaje de error al usuario
            model.addAttribute("mensaje", "Error: El ID del paciente ya existe");
            return "paciente/formularioNuevoPaciente"; // Retornar al formulario de creación con un mensaje de error
        }

        // Guardar el paciente si el ID no está duplicado
        pacienteRepository.save(paciente);
        model.addAttribute("mensaje", "Paciente creado correctamente");
        return "paciente/respuestaCreacionPaciente";
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @GetMapping("/all")
    public @ResponseBody Iterable<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @GetMapping("/all_frontend")
    public String listarTodos_frontend(Model model) {
        ArrayList<Paciente> lista = (ArrayList<Paciente>) pacienteRepository.findAll();
        model.addAttribute("pacientes", lista);
        return "paciente/listadoPacientes";
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @GetMapping("/find")
    public @ResponseBody Paciente buscarId(@RequestParam int id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con el ID: " + id));
    }
    
    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @CrossOrigin
    @GetMapping(path = "/update/{id}")
    public String editarViewPaciente(Model model, @PathVariable("id") int id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient Id:" + id));
        model.addAttribute("paciente", paciente);

        return "paciente/formularioActualizarPaciente";
    }

    @PreAuthorize("hasAnyRole('ODONTOLOGO', 'AUXILIAR')")
    @PostMapping("/updateDB/{id}")
    public String actualizarPaciente(@PathVariable("id") int id, @ModelAttribute Paciente paciente,
                                     BindingResult result, RedirectAttributes redirectAttributes) {
        paciente.setId(id);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el paciente");
            return "redirect:/pacientes/all_frontend";
        }

        pacienteRepository.save(paciente);

        redirectAttributes.addFlashAttribute("mensaje", "Paciente actualizado correctamente");
        return "redirect:/pacientes/all_frontend";
    }

    @PreAuthorize("hasRole('ODONTOLOGO')")
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable int id, Model model) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con el ID: " + id));
        model.addAttribute("paciente", paciente);
        return "paciente/confirmarEliminarPaciente"; 
    }

    @PreAuthorize("hasRole('ODONTOLOGO')")
    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable int id) {
        // Obtener todos los tratamientos asociados al paciente
        Iterable<Tratamiento> tratamientos = tratamientoRepository.findAll();
        
        // Iterar sobre los tratamientos y eliminar los asociados al paciente
        for (Tratamiento tratamiento : tratamientos) {
            if (tratamiento.getPaciente().getId() == id) {
                tratamientoRepository.delete(tratamiento);
            }
        }

        // Obtener todas las citas asociadas al paciente
        Iterable<Cita> citas = citaRepository.findAll();

        // Iterar sobre las citas y eliminar las asociadas al paciente
        for (Cita cita : citas) {
            if (cita.getPaciente().getId() == id) {
                citaRepository.delete(cita);
            }
        }
        
        // Eliminar al paciente
        pacienteRepository.deleteById(id);

        return "redirect:/pacientes/all_frontend"; 
    }
}
