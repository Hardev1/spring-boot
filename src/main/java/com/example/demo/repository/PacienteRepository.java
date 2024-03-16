package com.example.demo.repository;

import com.example.demo.entity.Paciente;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad Paciente, que gestiona la persistencia de los pacientes en la base de datos.
 */
public interface PacienteRepository extends CrudRepository<Paciente, Integer> {

    /**
     * Busca pacientes por nombres o apellidos que contengan la cadena de texto especificada,
     * o por número de identificación.
     * La búsqueda se realiza de forma insensible a mayúsculas y minúsculas.
     * @param nombres Cadena de texto a buscar en los nombres del paciente.
     * @param apellidos Cadena de texto a buscar en el apellido del paciente.
     * @param id Número de identificación a buscar.
     * @param pageable Objeto Pageable para permitir la paginación de resultados.
     * @return Una página de pacientes que cumplen con los criterios de búsqueda.
     */
    Page<Paciente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrId(
            String nombres, String apellidos, Integer id, Pageable pageable);

    /**
     * Devuelve una página que contiene todos los pacientes disponibles.
     * @param pageable Objeto Pageable para permitir la paginación de resultados.
     * @return Una página de todos los pacientes disponibles.
     */
    List<Paciente> findAll();
    Page<Paciente> findAll(Pageable pageable);
}
