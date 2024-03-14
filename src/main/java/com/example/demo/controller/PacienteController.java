package com.example.demo.controller;

import com.example.demo.entity.Paciente;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

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

    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo() {
        return "formularioNuevoPaciente";
    }

    @PostMapping("/new_frontend")
    public String nuevo(@Valid Paciente paciente, Model model) {
        // Verificar si ya existe un paciente con el mismo ID
        if (pacienteRepository.existsById(paciente.getId())) {
            // Manejar el caso de ID duplicado aquí, por ejemplo, generando un nuevo ID automáticamente
            // O mostrar un mensaje de error al usuario
            model.addAttribute("mensaje", "Error: El ID del paciente ya existe");
            return "formularioNuevoPaciente"; // Retornar al formulario de creación con un mensaje de error
        }

        // Guardar el paciente si el ID no está duplicado
        pacienteRepository.save(paciente);
        model.addAttribute("mensaje", "Paciente creado correctamente");
        return "respuestaCreacionPaciente";
    }

    @GetMapping("/all")
    public @ResponseBody Iterable<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @GetMapping("/all_frontend")
    public String listarTodos_frontend(Model model) {
        ArrayList<Paciente> lista = (ArrayList<Paciente>) pacienteRepository.findAll();
        model.addAttribute("pacientes", lista);
        return "listadoPacientes";
    }

    @GetMapping("/find")
    public @ResponseBody Paciente buscarId(@RequestParam int id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con el ID: " + id));
    }

    @PostMapping("/update/{id}")
    public @ResponseBody String actualizar(@PathVariable int id,
                                            @RequestParam String telefono,
                                            @RequestParam String direccion,
                                            @RequestParam String email) {
        Paciente paciente = buscarId(id);
        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);
        paciente.setEmail(email);
        pacienteRepository.save(paciente);
        return "formularioModificarPaciente";
    }

    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable int id, Model model) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con el ID: " + id));
        model.addAttribute("paciente", paciente);
        return "confirmarEliminarPaciente";
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable int id) {
        pacienteRepository.deleteById(id);
        return "redirect:/pacientes/all_frontend";
    }
}
