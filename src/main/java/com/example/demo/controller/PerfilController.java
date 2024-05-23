package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Perfil;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PerfilRepository;
import com.example.demo.security.CustomUserDetails;

@Controller
@RequestMapping("/perfiles")
public class PerfilController {

    @Autowired
    private PerfilRepository perfilRepository;

    @ModelAttribute("usuarioAutenticado")
    public CustomUserDetails getUserDetails(Principal principal) {
        if (principal != null) {
            return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public String listarTodosLosPerfiles(Model model) {
        List<Perfil> perfiles = perfilRepository.findAll();
        model.addAttribute("perfiles", perfiles);
        return "perfil/listadoPerfiles";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String mostrarFormularioNuevoPerfil(Model model) {
        model.addAttribute("perfil", new Perfil());
        return "perfil/formularioNuevoPerfil";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String crearNuevoPerfil(@ModelAttribute Perfil perfil, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear el perfil");
            return "redirect:/perfiles/new";
        }
        perfilRepository.save(perfil);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil creado correctamente");
        return "redirect:/perfiles/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditarPerfil(@PathVariable Long id, Model model) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con el ID: " + id));
        model.addAttribute("perfil", perfil);
        return "perfil/formularioActualizarPerfil";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String actualizarPerfil(@PathVariable Long id, @ModelAttribute Perfil perfil, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el perfil");
            return "redirect:/perfiles/edit/" + id;
        }
        perfilRepository.save(perfil);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        return "redirect:/perfiles/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminarPerfil(@PathVariable Long id, Model model) {
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con el ID: " + id));
        model.addAttribute("perfil", perfil);
        return "perfil/confirmarEliminarPerfil";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String eliminarPerfil(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        perfilRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Perfil eliminado correctamente");
        return "redirect:/perfiles/all";
    }
}