package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.TramoDeposito;
import com.tpi.operaciones.service.TramoDepositoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones/tramo-deposito")
public class TramoDepositoController {
    @Autowired
    private TramoDepositoService tramoDepositoService;

    @GetMapping
    public List<TramoDeposito> getAll() {
        return tramoDepositoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramoDeposito> getById(@PathVariable Long id) {
        return tramoDepositoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TramoDeposito create(@RequestBody TramoDeposito tramoDeposito) {
        return tramoDepositoService.save(tramoDeposito);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tramoDepositoService.delete(id);
    }
}

