package com.example.demo.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Odontologo;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.security.CustomUserDetails;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


@Controller
@RequestMapping(path="/odontologo")
public class OdontologoController {

	@Autowired
	private OdontologoRepository odontologoRepository;
	
	@ModelAttribute("usuarioAutenticado")
    public CustomUserDetails getUserDetails(Principal principal) {
        if (principal != null) {
            return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
	
	@PreAuthorize("hasRole('ADMIN')")
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
	
	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new_frontend")
    public String mostrarFormularioNuevo() {
        return "odontologo/formularioNuevoOdontologo"; 
    }
    
	@PreAuthorize("hasRole('ADMIN')")
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
        return "odontologo/respuestaCreacionOdontologo"; 
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(path="/all")
	public @ResponseBody Iterable <Odontologo> listarTodos(){
		return odontologoRepository.findAll();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(path="/all_frontend")
	public String listarTodos_frontend(Model modelo){
		ArrayList<Odontologo> lista = (ArrayList<Odontologo>) odontologoRepository.findAll();
		modelo.addAttribute("odontologos",lista);
		return "odontologo/listadoOdontologos";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(path="/find")
	public @ResponseBody Odontologo buscarId(@RequestParam int id){
		return odontologoRepository.findById(id).get();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
    @GetMapping(path = "/update/{id}")
    public String editarView(Model model, @PathVariable("id") int id) {
        Odontologo user = odontologoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);

        return "odontologo/formularioActualizarOdontologo";
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/updateDB/{id}")
	public String actualizar(@PathVariable("id") int id, @ModelAttribute Odontologo odontologo,
	                          BindingResult result, RedirectAttributes redirectAttributes) {
	    odontologo.setId(id);
	    if (result.hasErrors()) {
	        redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el odontólogo");
	        return "redirect:/odontologo/all_frontend";
	    }

	    odontologoRepository.save(odontologo);

	    redirectAttributes.addFlashAttribute("mensaje", "Odontólogo actualizado correctamente");
	    return "redirect:/odontologo/all_frontend";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable int id, Model model) {
        Odontologo odontologo = odontologoRepository.findById(id).get();
        model.addAttribute("odontologo", odontologo);
        return "odontologo/confirmarEliminarOdontologo"; 
    }

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/delete/{id}")
    public String eliminar(@PathVariable int id) {
        odontologoRepository.deleteById(id);
        return "redirect:/odontologo/all_frontend"; 
    }
}
