package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Solicitud;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {
    @Autowired
    private SolicitudRepository solicitudRepository;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public Optional<Solicitud> findById(Long id) {
        return solicitudRepository.findById(id);
    }

    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public void delete(Long id) {
        solicitudRepository.deleteById(id);
    }
}

