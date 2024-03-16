package com.example.demo.repository;

import com.example.demo.entity.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    Page<Cita> findByPaciente_Id(Integer pacienteId, Pageable pageable);

    List<Cita> findByFechaHoraBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Cita> findByFechaHora(LocalDateTime fechaHora);
}