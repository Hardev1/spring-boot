package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    // Puedes agregar consultas personalizadas si es necesario
	Perfil findByNombre (String nombre);
}