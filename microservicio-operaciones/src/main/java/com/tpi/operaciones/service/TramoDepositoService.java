package com.tpi.operaciones.service;

import com.tpi.operaciones.model.TramoDeposito;
import com.tpi.operaciones.repository.TramoDepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TramoDepositoService {
    @Autowired
    private TramoDepositoRepository tramoDepositoRepository;

    public List<TramoDeposito> findAll() {
        return tramoDepositoRepository.findAll();
    }

    public Optional<TramoDeposito> findById(Long id) {
        return tramoDepositoRepository.findById(id);
    }

    public TramoDeposito save(TramoDeposito tramoDeposito) {
        return tramoDepositoRepository.save(tramoDeposito);
    }

    public void delete(Long id) {
        tramoDepositoRepository.deleteById(id);
    }
}

