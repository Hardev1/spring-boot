package com.example.demo.controller;

import com.example.demo.entity.Cita;
import com.example.demo.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agenda")
public class AgendaController {

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping("/diaria")
    public ResponseEntity<List<Cita>> verAgendaDiaria(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(23, 59, 59);
        List<Cita> citasDelDia = citaRepository.findByFechaHoraBetween(inicioDelDia, finDelDia);
        return ResponseEntity.ok(citasDelDia);
    }

    @GetMapping("/semanal")
    public ResponseEntity<List<Cita>> verAgendaSemanal(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
    	
    	LocalDateTime inicioDeLaSemana = fecha.minusDays(fecha.getDayOfWeek().getValue() - 1).atStartOfDay();

    	LocalDateTime finDeLaSemana = inicioDeLaSemana.plusDays(6);
    	finDeLaSemana = finDeLaSemana.withHour(23).withMinute(59).withSecond(59);
        List<Cita> citasDeLaSemana = citaRepository.findByFechaHoraBetween(inicioDeLaSemana, finDeLaSemana);
        return ResponseEntity.ok(citasDeLaSemana);
    }

    @GetMapping("/mensual")
    public ResponseEntity<List<Cita>> verAgendaMensual(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDateTime inicioDelMes = fecha.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finDelMes = fecha.withDayOfMonth(fecha.lengthOfMonth()).atTime(23, 59, 59);
        List<Cita> citasDelMes = citaRepository.findByFechaHoraBetween(inicioDelMes, finDelMes);
        return ResponseEntity.ok(citasDelMes);
    }
}
