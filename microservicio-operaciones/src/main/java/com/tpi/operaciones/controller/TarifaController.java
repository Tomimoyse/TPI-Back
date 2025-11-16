package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Tarifa;
import com.tpi.operaciones.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones/tarifas")
public class TarifaController {
    @Autowired
    private TarifaService tarifaService;

    @GetMapping
    public List<Tarifa> getAll() {
        return tarifaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarifa> getById(@PathVariable Long id) {
        return tarifaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tarifa create(@RequestBody Tarifa tarifa) {
        return tarifaService.save(tarifa);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tarifaService.delete(id);
    }
}

