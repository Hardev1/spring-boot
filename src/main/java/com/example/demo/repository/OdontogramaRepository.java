package com.example.demo.repository;

import com.example.demo.entity.Odontograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OdontogramaRepository extends JpaRepository<Odontograma, Long> {
    // Aquí puedes agregar métodos personalizados de consulta si es necesario
}