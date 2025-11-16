package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.repository.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepositoService {
    @Autowired
    private DepositoRepository depositoRepository;

    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    public Optional<Deposito> findById(Long id) {
        return depositoRepository.findById(id);
    }

    public Deposito save(Deposito deposito) {
        return depositoRepository.save(deposito);
    }

    public void delete(Long id) {
        depositoRepository.deleteById(id);
    }
}

