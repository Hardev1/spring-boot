package com.example.demo.controller;

import com.example.demo.entity.Cita;
import com.example.demo.repository.CitaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/agenda")
public class AgendaController {

    private final CitaRepository citaRepository;

    // Constructor para inyección de dependencias
    public AgendaController(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @GetMapping("/agenda_diaria")
    public String verAgendaDiaria(Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        List<Cita> citasDiarias = citaRepository.findByFechaHoraBetween(startOfDay, endOfDay);
        model.addAttribute("citas", citasDiarias);
        return "cita/listadoCitas";
    }

    @GetMapping("/agenda_semanal")
    public String verAgendaSemanal(Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate();
        LocalDate endDate = startDate.plusDays(6); // Fin de la semana
        LocalDateTime startOfWeek = startDate.atStartOfDay();
        LocalDateTime endOfWeek = endDate.atTime(23, 59, 59);
        List<Cita> citasSemanales = citaRepository.findByFechaHoraBetween(startOfWeek, endOfWeek);
        model.addAttribute("citas", citasSemanales);
        return "cita/listadoCitas";
    }

    @GetMapping("/agenda_mensual")
    public String verAgendaMensual(Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.withDayOfMonth(1).toLocalDate(); // Primer día del mes
        LocalDate endDate = startDate.plusMonths(1).minusDays(1); // Último día del mes
        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.atTime(23, 59, 59);
        List<Cita> citasMensuales = citaRepository.findByFechaHoraBetween(startOfMonth, endOfMonth);
        model.addAttribute("citas", citasMensuales);
        return "cita/listadoCitas";
    }
}
