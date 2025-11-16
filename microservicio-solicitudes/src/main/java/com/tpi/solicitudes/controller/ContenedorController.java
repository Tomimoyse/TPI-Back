package com.tpi.solicitudes.controller;

import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.service.ContenedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes/contenedores")
public class ContenedorController {
    @Autowired
    private ContenedorService contenedorService;

    @GetMapping
    public List<Contenedor> getAll() {
        return contenedorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> getById(@PathVariable Long id) {
        return contenedorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Contenedor create(@RequestBody Contenedor contenedor) {
        return contenedorService.save(contenedor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contenedorService.delete(id);
    }
}

