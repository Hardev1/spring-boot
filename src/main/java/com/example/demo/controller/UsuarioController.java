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
import com.example.demo.entity.Usuario;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PerfilRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.CustomUserDetails;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public String listarTodosLosUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listadoUsuarios";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String mostrarFormularioNuevoUsuario(Model model) {
        List<Perfil> perfiles = perfilRepository.findAll();
        model.addAttribute("perfiles", perfiles);
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formularioNuevoUsuario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String crearNuevoUsuario(@ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al crear el usuario");
            return "redirect:/usuarios/new";
        }
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        return "redirect:/usuarios/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + id));
        List<Perfil> perfiles = perfilRepository.findAll();
        model.addAttribute("perfiles", perfiles);
        model.addAttribute("usuario", usuario);
        return "usuarios/formularioEditarUsuario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar el usuario");
            return "redirect:/usuarios/edit/" + id;
        }
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/usuarios/all";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String mostrarConfirmacionEliminarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + id));
        model.addAttribute("usuario", usuario);
        return "usuarios/confirmarEliminarUsuario";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        return "redirect:/usuarios/all";
    }
}