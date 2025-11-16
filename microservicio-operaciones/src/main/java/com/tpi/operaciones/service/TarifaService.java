package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Tarifa;
import com.tpi.operaciones.repository.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarifaService {
    @Autowired
    private TarifaRepository tarifaRepository;

    public List<Tarifa> findAll() {
        return tarifaRepository.findAll();
    }

    public Optional<Tarifa> findById(Long id) {
        return tarifaRepository.findById(id);
    }

    public Tarifa save(Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    public void delete(Long id) {
        tarifaRepository.deleteById(id);
    }
}

