package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "odontograma")
public class Odontograma {

	public byte[] getPdfOdontograma() {
		return pdfOdontograma;
	}

	public void setPdfOdontograma(byte[] pdfOdontograma) {
		this.pdfOdontograma = pdfOdontograma;
	}

	public static List<String> getListaestadosdentales() {
		return listaEstadosDentales;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "paciente_id")
	private Paciente paciente;

	@ManyToOne
	@JoinColumn(name = "odontologo_id")
	private Odontologo odontologo;

	@OneToMany(mappedBy = "odontograma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDiente> detallesDientes = new ArrayList<>();
	
	@Column(name = "fecha_creacion")
	private LocalDateTime fechaCreacion;

	@Column(name = "ultima_modificacion")
	private LocalDateTime ultimaModificacion;

	@Column(name = "guardado_automatico")
	private boolean guardadoAutomatico;

	@Lob
	@Column(name = "imagen_odontograma")
	private byte[] imagenOdontograma;

	@Lob
	@Column(name = "pdf_odontograma", columnDefinition = "LONGBLOB")
	private byte[] pdfOdontograma;

	@Column(name = "formato_imagen")
	private String formatoImagen;

	@Column(name = "comentarios_generales")
	private String comentariosGenerales;

	public static final List<String> listaEstadosDentales = Arrays.asList("Estado indefinido", "Diente Ausente",
			"Corona en buen estado", "Obturación en buen estado", "Sellante de fosas y fisuras en buen estado",
			"Prótesis parcial fija en buen estado", "Presencia de aparato de ortodoncia",
			"Diente Indicado a extracción", "Corona en mal estado", "Obturación en mal estado",
			"Sellante de fosas y fisuras en mal estado", "Prótesis parcial fija en mal estado",
			"Fractura de corona dental", "Caries dental");

	// Constructor, getters y setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Odontologo getOdontologo() {
		return odontologo;
	}

	public void setOdontologo(Odontologo odontologo) {
		this.odontologo = odontologo;
	}

	public List<DetalleDiente> getDetallesDientes() {
        return detallesDientes;
    }

    public void setDetallesDientes(List<DetalleDiente> detallesDientes) {
        this.detallesDientes = detallesDientes;
    }

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDateTime getUltimaModificacion() {
		return ultimaModificacion;
	}

	public void setUltimaModificacion(LocalDateTime ultimaModificacion) {
		this.ultimaModificacion = ultimaModificacion;
	}

	public boolean isGuardadoAutomatico() {
		return guardadoAutomatico;
	}

	public void setGuardadoAutomatico(boolean guardadoAutomatico) {
		this.guardadoAutomatico = guardadoAutomatico;
	}

	public byte[] getImagenOdontograma() {
		return imagenOdontograma;
	}

	public void setImagenOdontograma(byte[] imagenOdontograma) {
		this.imagenOdontograma = imagenOdontograma;
	}

	public String getFormatoImagen() {
		return formatoImagen;
	}

	public void setFormatoImagen(String formatoImagen) {
		this.formatoImagen = formatoImagen;
	}

	public String getComentariosGenerales() {
		return comentariosGenerales;
	}

	public void setComentariosGenerales(String comentariosGenerales) {
		this.comentariosGenerales = comentariosGenerales;
	}

	public void exportarOdontograma(String formato) {
		ExportadorOdontograma exportador = ObtenerExportador(formato);
		if (exportador != null) {
			exportador.exportar(this);
		} else {
			// Lógica para manejar formatos no soportados
		}
	}

	private ExportadorOdontograma ObtenerExportador(String formato) {
		if (formato.equalsIgnoreCase("PDF")) {
			return new ExportadorPDF();
		} else if (formato.equalsIgnoreCase("imagen")) {
			return new ExportadorImagen();
		}
		return null;
	}
}

interface ExportadorOdontograma {
	void exportar(Odontograma odontograma);
}

class ExportadorPDF implements ExportadorOdontograma {
	@Override
	public void exportar(Odontograma odontograma) {
		// Lógica para exportar el odontograma a PDF
	}
}

class ExportadorImagen implements ExportadorOdontograma {
	@Override
	public void exportar(Odontograma odontograma) {
		// Lógica para exportar el odontograma a imagen
	}
}