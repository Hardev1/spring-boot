package com.example.demo.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.entity.Usuario;
import com.example.demo.dto.LoginDto;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Rol;
import com.example.demo.repository.UsuarioRepository;

@Service
public class CustomDetailsService implements UserDetailsService {
    private UsuarioRepository usuarioRepository;
    private LoginDto loginDto;

    public CustomDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correoElectronico) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correoElectronico)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<GrantedAuthority> autoridades = new HashSet<>();
        Rol rol = getRolFromPerfil(usuario.getPerfil());
        autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.name()));

        // Verifica si la contraseña proporcionada coincide con la contraseña almacenada
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatches = passwordEncoder.matches(usuario.getClave(), loginDto.getPassword());

        System.out.println("Contraseña almacenada: " + usuario.getClave());
        System.out.println("Contraseña proporcionada: password");

        if (passwordMatches) {
            return (UserDetails) new CustomUserDetails(
                    usuario.getCorreoElectronico(),
                    usuario.getClave(),
                    autoridades,
                    usuario.getNombre(),
                    rol
            );
        } else {
        	System.out.println("efe");
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    private Rol getRolFromPerfil(Perfil perfil) {
        switch (perfil.getNombre().toUpperCase()) {
            case "ADMIN":
                return Rol.ADMIN;
            case "ODONTOLOGO":
                return Rol.ODONTOLOGO;
            case "AUXILIAR":
                return Rol.AUXILIAR;
            case "PACIENTE":
                return Rol.PACIENTE;
            default:
                throw new IllegalArgumentException("Perfil no válido");
        }
    }
}