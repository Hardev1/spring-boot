package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DetalleDiente;
import com.example.demo.entity.Odontograma;

public interface DetalleDienteRepository extends JpaRepository<DetalleDiente, Long> {

    /**
     * Find all DetalleDiente entities by Odontograma
     * 
     * @param odontograma the Odontograma entity
     * @return a list of DetalleDiente entities
     */
    List<DetalleDiente> findByOdontograma(Odontograma odontograma);

    /**
     * Find a DetalleDiente entity by its position and Odontograma
     * 
     * @param posicionDiente the position of the diente
     * @param odontograma the Odontograma entity
     * @return the DetalleDiente entity or null if not found
     */
    DetalleDiente findByPosicionDienteAndOdontograma(String posicionDiente, Odontograma odontograma);

    /**
     * Find all DetalleDiente entities by estado
     * 
     * @param estado the estado of the diente
     * @return a list of DetalleDiente entities
     */
    List<DetalleDiente> findByEstado(String estado);
}