package com.example.demo.repository;

import com.example.demo.entity.Tratamiento;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad Tratamiento, que gestiona la persistencia de los tratamientos en la base de datos.
 */
public interface TratamientoRepository extends CrudRepository<Tratamiento, Integer> {

    /**
     * Busca tratamientos por tipo de tratamiento que contenga la cadena de texto especificada,
     * o por fecha.
     * La búsqueda se realiza de forma insensible a mayúsculas y minúsculas.
     * @param tipoTratamiento Cadena de texto a buscar en el tipo de tratamiento.
     * @param fecha Fecha del tratamiento a buscar.
     * @param pageable Objeto Pageable para permitir la paginación de resultados.
     * @return Una página de tratamientos que cumplen con los criterios de búsqueda.
     */
    Page<Tratamiento> findByTipoTratamientoContainingIgnoreCaseOrFecha(String tipoTratamiento, LocalDate fecha, Pageable pageable);

    /**
     * Devuelve una página que contiene todos los tratamientos disponibles.
     * @param pageable Objeto Pageable para permitir la paginación de resultados.
     * @return Una página de todos los tratamientos disponibles.
     */
    Page<Tratamiento> findAll(Pageable pageable);
}
