package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Odontologo;

public interface OdontologoRepository extends CrudRepository<Odontologo, Integer>{
	List<Odontologo> findAll();
}
