package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.repository.ContenedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenedorService {
    @Autowired
    private ContenedorRepository contenedorRepository;

    public List<Contenedor> findAll() {
        return contenedorRepository.findAll();
    }

    public Optional<Contenedor> findById(Long id) {
        return contenedorRepository.findById(id);
    }

    public Contenedor save(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    public void delete(Long id) {
        contenedorRepository.deleteById(id);
    }
}

