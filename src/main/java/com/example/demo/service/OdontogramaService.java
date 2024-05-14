package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.demo.entity.Odontograma;

public interface OdontogramaService {
	
	void crearOdontograma(String[] dientesEvaluados, String comentariosGenerales, int pacienteId, int odontologoId,
			Map<String, String> notasDientesParams, List<String> botonesVisibles)
			throws IOException;
	
	void eliminarOdontograma(Long id);
	
	void actualizarOdontograma(Odontograma odontograma, String[] dientesEvaluados, String comentariosGenerales,
			int pacienteId, int odontologoId, Map<String, String> notasDientesParams, List<String> botonesVisibles)
			throws IOException;
}