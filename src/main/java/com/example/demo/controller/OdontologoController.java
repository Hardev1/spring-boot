package com.example.demo.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.example.demo.entity.Odontologo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OdontologoRepository;

@Controller
@RequestMapping(path="/odontologo")
public class OdontologoController {

    @Autowired
    private OdontologoRepository odontologoRepository;

    @PostMapping(path="/new")
    public @ResponseBody String nuevo(@RequestParam String nombres, 
            @RequestParam String apellidos, 
            @RequestParam String especialidad, 
            @RequestParam String telefono, 
            @RequestParam String email) {
        Odontologo o = new Odontologo();
        o.setNombres(nombres);
        o.setApellidos(apellidos);
        o.setEspecialidad(especialidad);
        o.setTelefono(telefono);
        o.setEmail(email);

        odontologoRepository.save(o);
        return "Listo";
    }

    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo() {
        return "formularioNuevoOdontologo"; 
    }

    @PostMapping("/new_frontend")
    public String nuevo(@RequestParam String nombres,
                        @RequestParam String apellidos,
                        @RequestParam String especialidad,
                        @RequestParam String telefono,
                        @RequestParam String email,
                        Model model) {
        Odontologo o = new Odontologo();
        o.setNombres(nombres);
        o.setApellidos(apellidos);
        o.setEspecialidad(especialidad);
        o.setTelefono(telefono);
        o.setEmail(email);

        odontologoRepository.save(o);

        model.addAttribute("mensaje", "Odontólogo creado correctamente");
        return "respuestaCreacionOdontologo"; 
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Odontologo> listarTodos(){
        return odontologoRepository.findAll();
    }

    @GetMapping(path="/all_frontend")
    public String listarTodos_frontend(Model model){
        ArrayList<Odontologo> lista = (ArrayList<Odontologo>) odontologoRepository.findAll();
        model.addAttribute("odontologos",lista);
        return "listadoOdontologos";
    }

    @GetMapping(path="/find")
    public @ResponseBody Odontologo buscarId(@RequestParam int id){
        return odontologoRepository.findById(id).orElse(null);
    }
    
    @GetMapping(path = "/find_odontologo/{id}")
    public @ResponseBody Odontologo buscarOdontologoPorId(@PathVariable int id) {
        return odontologoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Odontólogo no encontrado con el ID: " + id));
    }
    
    /*
    @PostMapping(path="/update/{id}")
    public String actualizar(@PathVariable int id,
                                 @ModelAttribute Odontologo odontologo, Model model) {
            Odontologo o = odontologoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Odontólogo no encontrado con id: " + id));

            o.setNombres(odontologo.getNombres());
            o.setApellidos(odontologo.getApellidos());
            o.setEspecialidad(odontologo.getEmail());
            o.setTelefono(odontologo.getTelefono());
            o.setEmail(odontologo.getEmail());

            odontologoRepository.save(o);

            model.addAttribute("mensaje", "Odontólogo actualizado correctamente");
            return "listadoOdontologos"; // Redirect to listadoOdontologos view after update
    }*/

    @PostMapping("/update/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Odontologo odontologo, Model model) {
        Odontologo o = odontologoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Odontólogo no encontrado con id: " + id));

        // Actualizar los atributos del odontólogo con los valores recibidos del formulario
        o.setNombres(odontologo.getNombres());
        o.setApellidos(odontologo.getApellidos());
        o.setEspecialidad(odontologo.getEspecialidad());
        o.setTelefono(odontologo.getTelefono());
        o.setEmail(odontologo.getEmail());

        // Guardar los cambios en la base de datos
        odontologoRepository.save(o);

        // Agregar un mensaje de éxito al modelo para mostrar en la vista
        model.addAttribute("mensaje", "Odontólogo actualizado correctamente");

        // Redirigir al listado de odontólogos después de la actualización
        return "redirect:/odontologo/all_frontend";
    }
    
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable int id, Model model) {
        
            model.addAttribute("id", id);
            return "confirmarEliminarOdontologo"; 
        
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable int id) {
        odontologoRepository.deleteById(id);
        return "redirect:/odontologo/all_frontend"; 
    } 
}
