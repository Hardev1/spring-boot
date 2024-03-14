package com.example.demo.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Tratamiento;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TratamientoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @PostMapping("/crear")
    public ResponseEntity<Tratamiento> crearTratamiento(@Valid @RequestBody Tratamiento tratamiento) {
        Tratamiento nuevoTratamiento = tratamientoRepository.save(tratamiento);
        return new ResponseEntity<>(nuevoTratamiento, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tratamiento> obtenerTratamientoPorId(@PathVariable int id) {
        Optional<Tratamiento> tratamientoOptional = tratamientoRepository.findById(id);
        if (tratamientoOptional.isPresent()) {
            Tratamiento tratamiento = tratamientoOptional.get();
            return ResponseEntity.ok(tratamiento);
        } else {
            throw new ResourceNotFoundException("Tratamiento no encontrado con el ID: " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tratamiento> actualizarTratamiento(@PathVariable int id, @RequestBody Map<String, Object> cambios) {
        Optional<Tratamiento> tratamientoOptional = tratamientoRepository.findById(id);
        if (tratamientoOptional.isPresent()) {
            Tratamiento tratamiento = tratamientoOptional.get();
            for (Map.Entry<String, Object> entry : cambios.entrySet()) {
                String propiedad = entry.getKey();
                Object valor = entry.getValue();
                switch (propiedad) {
                    case "tipoTratamiento":
                        tratamiento.setTipoTratamiento((String) valor);
                        break;
                    case "fecha":
                        tratamiento.setFecha((LocalDate) valor);
                        break;
                    // Agregar más casos según las propiedades que se puedan actualizar
                    default:
                        return ResponseEntity.badRequest().build(); // Devuelve un ResponseEntity vacío
                }
            }
            tratamientoRepository.save(tratamiento);
            return ResponseEntity.ok(tratamiento); // Devuelve el tratamiento actualizado
        } else {
            throw new ResourceNotFoundException("Tratamiento no encontrado con el ID: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTratamiento(@PathVariable int id) {
        Optional<Tratamiento> tratamientoOptional = tratamientoRepository.findById(id);
        if (tratamientoOptional.isPresent()) {
            Tratamiento tratamiento = tratamientoOptional.get();
            tratamientoRepository.delete(tratamiento);
            return ResponseEntity.ok("Tratamiento eliminado exitosamente");
        } else {
            throw new ResourceNotFoundException("Tratamiento no encontrado con el ID: " + id);
        }
    }
}
