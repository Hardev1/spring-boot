package com.example.demo.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.builder.OdontogramaBuilder;
import com.example.demo.entity.DetalleDiente;
import com.example.demo.entity.Odontograma;
import com.example.demo.entity.Odontologo;
import com.example.demo.entity.Paciente;
import com.example.demo.repository.DetalleDienteRepository;
import com.example.demo.repository.OdontogramaRepository;
import com.example.demo.repository.OdontologoRepository;
import com.example.demo.repository.PacienteRepository;
import com.example.demo.service.OdontogramaService;
import com.example.demo.service.PdftoImageService;

@Service
public class OdontogramaServiceImpl implements OdontogramaService {

	@Autowired
	private OdontogramaRepository odontogramaRepository;
	private final PacienteRepository pacienteRepository;
	private final OdontologoRepository odontologoRepository;
	private final DetalleDienteRepository detalleDienteRepository;
	private final PdfServiceImpl pdfServiceImpl;
	private final PdftoImageService pdftoImageService;

	public OdontogramaServiceImpl(OdontogramaRepository odontogramaRepository, PacienteRepository pacienteRepository,
			OdontologoRepository odontologoRepository, DetalleDienteRepository detalleDienteRepository,
			PdfServiceImpl pdfServiceImpl, PdftoImageService pdftoImageService) {
		this.odontogramaRepository = odontogramaRepository;
		this.pacienteRepository = pacienteRepository;
		this.odontologoRepository = odontologoRepository;
		this.detalleDienteRepository = detalleDienteRepository;
		this.pdfServiceImpl = pdfServiceImpl;
		this.pdftoImageService = pdftoImageService;
	}

	@Override
	public void crearOdontograma(String[] dientesEvaluados, String comentariosGenerales, int pacienteId,
			int odontologoId, Map<String, String> notasDientesParams, List<String> botonesVisibles) throws IOException {
		Paciente paciente = obtenerPaciente(pacienteId);
		Odontologo odontologo = obtenerOdontologo(odontologoId);
		LocalDateTime fechaActual = LocalDateTime.now();

		List<String> notasDientes = guardarDetallesDientes(dientesEvaluados, notasDientesParams);

		byte[] pdfBytes = pdfServiceImpl.generarPdfToByteArray(dientesEvaluados, paciente, odontologo, fechaActual,
				comentariosGenerales, botonesVisibles, notasDientes);
		byte[] imageBytes = convertPdfToImage(pdfBytes);

		Odontograma odontograma = crearOdontograma(paciente, odontologo, fechaActual, comentariosGenerales, pdfBytes,
				imageBytes);
		odontogramaRepository.save(odontograma);

		guardarDetallesDientes(dientesEvaluados, odontograma, notasDientesParams, notasDientes);
	}

	private Paciente obtenerPaciente(int pacienteId) {
		return pacienteRepository.findById(pacienteId)
				.orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
	}

	private Odontologo obtenerOdontologo(int odontologoId) {
		return odontologoRepository.findById(odontologoId)
				.orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
	}

	private byte[] convertPdfToImage(byte[] pdfBytes) throws IOException {
		return pdftoImageService.convertPdfToImage(pdfBytes);
	}

	private Odontograma crearOdontograma(Paciente paciente, Odontologo odontologo, LocalDateTime fechaActual,
			String comentariosGenerales, byte[] pdfBytes, byte[] imageBytes) {
		return new OdontogramaBuilder().withPaciente(paciente).withOdontologo(odontologo).withFechaCreacion(fechaActual)
				.withUltimaModificacion(fechaActual).withComentariosGenerales(comentariosGenerales)
				.withPdfOdontograma(pdfBytes).withImagenOdontograma(imageBytes).build();
	}

	private List<String> guardarDetallesDientes(String[] dientesEvaluados, Map<String, String> notasDientesParams) {
		List<String> notasDientes = new ArrayList<>();
		for (String diente : dientesEvaluados) {
			DetalleDiente detalleDiente = createDetalleDiente(diente);
			if (detalleDiente.getNota() != null && !detalleDiente.getNota().isEmpty()) {
				notasDientes.add(detalleDiente.getNota());
			}
		}
		return notasDientes;
	}

	private void guardarDetallesDientes(String[] dientesEvaluados, Odontograma odontograma,
			Map<String, String> notasDientesParams, List<String> notasDientes) {
		for (String diente : dientesEvaluados) {
			DetalleDiente detalleDiente = createDetalleDiente(diente);
			detalleDiente.setOdontograma(odontograma);
			detalleDienteRepository.save(detalleDiente);
		}
	}

	private DetalleDiente createDetalleDiente(String diente) {
		String[] partes = diente.split(" - ");
		String numeroDienteString = partes[0];
		String estadoDental = partes[1];
		String notaDiente = extractNotaDiente(diente);

		DetalleDiente detalleDiente = new DetalleDiente();
		detalleDiente.setPosicionDiente(numeroDienteString);
		detalleDiente.setEstado(estadoDental);
		detalleDiente.setNota(notaDiente);
		return detalleDiente;
	}

	private String extractNotaDiente(String diente) {
		String[] parteNota = diente.split(" - Nota: ");
		return parteNota.length > 1 ? parteNota[1] : "";
	}

	@Override
	public void eliminarOdontograma(Long id) {
		Optional<Odontograma> odontogramaOptional = odontogramaRepository.findById(id);
		if (odontogramaOptional.isPresent()) {
			odontogramaRepository.delete(odontogramaOptional.get());
		} else {
			throw new IllegalArgumentException("El odontograma con ID " + id + " no existe.");
		}
	}
	
	@Override
	public void actualizarOdontograma(Odontograma odontograma, String[] dientesEvaluados, String comentariosGenerales,
			int pacienteId, int odontologoId, Map<String, String> notasDientesParams, List<String> botonesVisibles)
			throws IOException {

		Paciente paciente = obtenerPaciente(pacienteId);
		Odontologo odontologo = obtenerOdontologo(odontologoId);
		LocalDateTime fechaActual = LocalDateTime.now();

		List<String> notasDientes = guardarDetallesDientes(dientesEvaluados, notasDientesParams);

		byte[] pdfBytes = pdfServiceImpl.generarPdfToByteArray(dientesEvaluados, paciente, odontologo, fechaActual,
				comentariosGenerales, botonesVisibles, notasDientes);
		byte[] imageBytes = convertPdfToImage(pdfBytes);

		odontograma.setPaciente(paciente);
		odontograma.setOdontologo(odontologo);
		odontograma.setFechaCreacion(odontograma.getFechaCreacion());
		odontograma.setUltimaModificacion(fechaActual);
		odontograma.setComentariosGenerales(comentariosGenerales);
		odontograma.setPdfOdontograma(pdfBytes);
		odontograma.setImagenOdontograma(imageBytes);

		odontogramaRepository.save(odontograma);

		guardarDetallesDientes(dientesEvaluados, odontograma, notasDientesParams, notasDientes);
	}

}