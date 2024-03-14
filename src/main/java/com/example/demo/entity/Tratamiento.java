package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "tipo_tratamiento", nullable = false)
    private String tipoTratamiento;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "procedimientos_realizados")
    private String procedimientosRealizados;

    @Column(name = "resultados")
    private String resultados;

    @Column(name = "medicamentos_recetados")
    private String medicamentosRecetados;

    @Column(name = "instrucciones_postoperatorias")
    private String instruccionesPostoperatorias;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getTipoTratamiento() {
        return tipoTratamiento;
    }

    public void setTipoTratamiento(String tipoTratamiento) {
        this.tipoTratamiento = tipoTratamiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate valor) {
        this.fecha = valor;
    }

    public String getProcedimientosRealizados() {
        return procedimientosRealizados;
    }

    public void setProcedimientosRealizados(String procedimientosRealizados) {
        this.procedimientosRealizados = procedimientosRealizados;
    }

    public String getResultados() {
        return resultados;
    }

    public void setResultados(String resultados) {
        this.resultados = resultados;
    }

    public String getMedicamentosRecetados() {
        return medicamentosRecetados;
    }

    public void setMedicamentosRecetados(String medicamentosRecetados) {
        this.medicamentosRecetados = medicamentosRecetados;
    }

    public String getInstruccionesPostoperatorias() {
        return instruccionesPostoperatorias;
    }

    public void setInstruccionesPostoperatorias(String instruccionesPostoperatorias) {
        this.instruccionesPostoperatorias = instruccionesPostoperatorias;
    }
}