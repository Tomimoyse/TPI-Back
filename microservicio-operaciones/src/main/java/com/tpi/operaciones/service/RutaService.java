package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Ruta;
import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.repository.RutaRepository;
import com.tpi.operaciones.repository.TramoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RutaService {
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private TramoRepository tramoRepository;

    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> findById(Long id) {
        return rutaRepository.findById(id);
    }

    public Ruta save(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    public void delete(Long id) {
        rutaRepository.deleteById(id);
    }

    @Transactional
    public Ruta asignarTramos(Long rutaId, List<Long> tramoIds) {
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        List<Tramo> tramos = tramoRepository.findAllById(tramoIds);
        ruta.setTramos(tramos);
        return rutaRepository.save(ruta);
    }

    public Boolean tieneTramos(Long rutaId) {
        return rutaRepository.findById(rutaId)
                .map(r -> r.getTramos() != null && !r.getTramos().isEmpty())
                .orElse(false);
    }
}
