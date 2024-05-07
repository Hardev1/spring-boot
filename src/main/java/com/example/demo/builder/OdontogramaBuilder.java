package com.example.demo.builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.DetalleDiente;
import com.example.demo.entity.Odontograma;
import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;

public class OdontogramaBuilder {

    private Long id;
    private Paciente paciente;
    private Odontologo odontologo;
    private List<DetalleDiente> detallesDientes = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaModificacion;
    private byte[] imagenOdontograma;
    private byte[] pdfOdontograma;
    private String comentariosGenerales;

    public OdontogramaBuilder withPaciente(Paciente paciente) {
        this.paciente = paciente;
        return this;
    }

    public OdontogramaBuilder withOdontologo(Odontologo odontologo) {
        this.odontologo = odontologo;
        return this;
    }

    public OdontogramaBuilder withDetallesDientes(List<DetalleDiente> detallesDientes) {
        this.detallesDientes = detallesDientes;
        return this;
    }

    public OdontogramaBuilder withFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
        return this;
    }

    public OdontogramaBuilder withUltimaModificacion(LocalDateTime ultimaModificacion) {
        this.ultimaModificacion = ultimaModificacion;
        return this;
    }

    public OdontogramaBuilder withImagenOdontograma(byte[] imagenOdontograma) {
        this.imagenOdontograma = imagenOdontograma;
        return this;
    }

    public OdontogramaBuilder withPdfOdontograma(byte[] pdfOdontograma) {
        this.pdfOdontograma = pdfOdontograma;
        return this;
    }

    public OdontogramaBuilder withComentariosGenerales(String comentariosGenerales) {
        this.comentariosGenerales = comentariosGenerales;
        return this;
    }

    public Odontograma build() {
        Odontograma odontograma = new Odontograma();
        odontograma.setId(id);
        odontograma.setPaciente(paciente);
        odontograma.setOdontologo(odontologo);
        odontograma.setDetallesDientes(detallesDientes);
        odontograma.setFechaCreacion(fechaCreacion);
        odontograma.setUltimaModificacion(ultimaModificacion);
        odontograma.setImagenOdontograma(imagenOdontograma);
        odontograma.setPdfOdontograma(pdfOdontograma);
        odontograma.setComentariosGenerales(comentariosGenerales);
        return odontograma;
    }
}