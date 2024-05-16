package com.example.demo.security;

import java.util.Collection;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.core.GrantedAuthority;

import com.example.demo.entity.Rol;

public class CustomUserDetails extends User {
    private String nombre;
    private Rol rol;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String nombre, Rol rol) {
        super(); // Constructor correcto de User
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }
}
