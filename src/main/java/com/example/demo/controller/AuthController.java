package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.RegisterDto;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.PerfilRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JWTGenerator;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private AuthenticationManager authenticationManager;
	private UsuarioRepository usuarioRepository;
	private PerfilRepository perfilRepository;
	private PasswordEncoder passwordEncoder;
	private JWTGenerator jwtGenerator;

	public AuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
			PerfilRepository perfilRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
		this.authenticationManager = authenticationManager;
		this.usuarioRepository = usuarioRepository;
		this.perfilRepository = perfilRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtGenerator = jwtGenerator;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
	    try {
	        Authentication authentication = authenticationManager
	                .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
	        
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        String token = jwtGenerator.generateToken(authentication);

	        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
	    } catch (AuthenticationCredentialsNotFoundException e) {
	        // Manejar la excepción lanzada por JWTGenerator
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
	    } catch (AuthenticationException e) {
	    	System.out.println(loginDto.getPassword());
	        return new ResponseEntity<>("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
	    } catch (Exception e) {
	        // Manejar otras excepciones inesperadas
	        return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {

		String username = registerDto.getUsername();

		if (usuarioRepository.findByCorreoElectronico(username).isPresent()) {
			return new ResponseEntity<>("Este usuario ya fue elegido", HttpStatus.BAD_REQUEST);
		}

		String nombre = extractNameFromEmail(username);

		Usuario usuario = new Usuario();
		usuario.setCorreoElectronico(username);
		usuario.setNombre(capitalizeFirstLetter(nombre));
		//usuario.setClave(passwordEncoder.encode((registerDto.getPassword())));
		usuario.setClave(registerDto.getPassword());

		Perfil perfil = perfilRepository.findByNombre("ADMIN");

		usuario.setPerfil(perfil);

		usuarioRepository.save(usuario);

		return ResponseEntity.ok("Usuario registrado exitosamente");
	}

	private String extractNameFromEmail(String email) {
		String[] parts = email.split("@")[0].split("\\.");
		return capitalizeFirstLetter(parts[0]);
	}

	private String capitalizeFirstLetter(String nombre) {
		return nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase();
	}
}
