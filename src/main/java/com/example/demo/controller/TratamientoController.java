package com.example.demo.controller;

import com.example.demo.entity.Paciente;
import com.example.demo.entity.Tratamiento;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tratamiento")
public class TratamientoController {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo(Model model) {
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("tratamiento", new Tratamiento());
        return "tratamiento/formularioNuevoTratamiento";
    }

    @PostMapping("/new_frontend")
    public String nuevo(@RequestParam int pacienteId,
                        @RequestParam String tipoTratamiento,
                        @RequestParam LocalDate fecha,
                        @RequestParam String procedimientosRealizados,
                        @RequestParam String resultados,
                        @RequestParam String medicamentosRecetados,
                        @RequestParam String instruccionesPostoperatorias,
                        Model model) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + pacienteId));

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setPaciente(paciente);
        tratamiento.setTipoTratamiento(tipoTratamiento);
        tratamiento.setFecha(fecha);
        tratamiento.setProcedimientosRealizados(procedimientosRealizados);
        tratamiento.setResultados(resultados);
        tratamiento.setMedicamentosRecetados(medicamentosRecetados);
        tratamiento.setInstruccionesPostoperatorias(instruccionesPostoperatorias);

        tratamientoRepository.save(tratamiento);

        model.addAttribute("mensaje", "Tratamiento creado correctamente");

        return "redirect:/tratamiento/all_frontend";
    }
    
    @GetMapping("/find")
    public @ResponseBody Tratamiento buscarId(@RequestParam int id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con el ID: " + id));
    }

    @CrossOrigin
    @GetMapping(path = "/update/{id}")
    public String editarViewTratamiento(Model model, @PathVariable("id") int id) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado con ID:" + id));
        String fechaFormateada = tratamiento.getFecha().toString();
        model.addAttribute("fechaFormateada", fechaFormateada);
        model.addAttribute("tratamiento", tratamiento);
        List<Paciente> pacientes = pacienteRepository.findAll();
        model.addAttribute("pacientes", pacientes);
        return "tratamiento/formularioActualizarTratamiento";
    }

    @PostMapping("/update/{id}")
    public String actualizarTratamiento(@PathVariable("id") int id, @ModelAttribute Tratamiento tratamiento,
                                         BindingResult result, RedirectAttributes redirectAttributes) {
        // Obtener el tratamiento existente por su ID
        Tratamiento tratamientoExistente = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado con ID: " + id));

        // Asignar el ID al tratamiento recibido
        tratamiento.setId(id);

        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el tratamiento");
            return "redirect:/tratamiento/all_frontend";
        }

        // Asignar el paciente del tratamiento existente al tratamiento recibido
        tratamiento.setPaciente(tratamientoExistente.getPaciente());

        // Guardar el tratamiento en la base de datos
        tratamientoRepository.save(tratamiento);

        // Agregar un mensaje al modelo para mostrar en la vista de confirmación
        redirectAttributes.addFlashAttribute("mensaje", "Tratamiento actualizado correctamente");
        return "redirect:/tratamiento/all_frontend";
    }

    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminarTratamiento(@PathVariable int id, Model model) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con el ID: " + id));
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento/confirmarEliminarTratamiento";
    }

    @PostMapping("/delete/{id}")
    public String eliminarTratamiento(@PathVariable int id) {
        tratamientoRepository.deleteById(id);
        return "redirect:/tratamiento/all_frontend";
    }


    @GetMapping("/all_frontend")
    public String listarTodos(Model model) {
        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
        model.addAttribute("tratamientos", tratamientos);
        return "tratamiento/listadoTratamientos";
    }

    @GetMapping("/all")
    public @ResponseBody Iterable<Tratamiento> listarTodos() {
        return tratamientoRepository.findAll();
    }
}
