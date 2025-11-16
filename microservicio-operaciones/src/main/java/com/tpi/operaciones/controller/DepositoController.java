package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.service.DepositoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones/depositos")
public class DepositoController {
    @Autowired
    private DepositoService depositoService;

    @GetMapping
    public List<Deposito> getAll() {
        return depositoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deposito> getById(@PathVariable Long id) {
        return depositoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Deposito create(@RequestBody Deposito deposito) {
        return depositoService.save(deposito);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        depositoService.delete(id);
    }
}

