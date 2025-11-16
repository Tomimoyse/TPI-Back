package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Camion;
import com.tpi.operaciones.service.CamionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operaciones/camiones")
public class CamionController {
    
    @Autowired
    private CamionService camionService;
    
    @GetMapping
    public List<Camion> getAll() {
        return camionService.findAll();
    }
    
    @GetMapping("/{dominio}")
    public ResponseEntity<Camion> getById(@PathVariable String dominio) {
        return camionService.findById(dominio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public Camion create(@RequestBody Camion camion) {
        return camionService.save(camion);
    }
    
    @PutMapping("/{dominio}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Camion> update(@PathVariable String dominio, @RequestBody Camion camion) {
        if (!camionService.findById(dominio).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        camion.setDominio(dominio);
        return ResponseEntity.ok(camionService.save(camion));
    }
    
    @DeleteMapping("/{dominio}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String dominio) {
        if (!camionService.findById(dominio).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        camionService.delete(dominio);
        return ResponseEntity.ok().build();
    }
    
    // Obtener camiones disponibles
    @GetMapping("/disponibles")
    public List<Camion> getDisponibles() {
        return camionService.findDisponibles();
    }
    
    // Obtener camiones aptos para un contenedor específico
    @GetMapping("/aptos")
    public List<Camion> getAptos(@RequestParam Double peso, @RequestParam Double volumen) {
        return camionService.findAptosParaContenedor(peso, volumen);
    }
    
    // Validar si un camión puede transportar un contenedor
    @GetMapping("/{dominio}/puede-transportar")
    public ResponseEntity<Boolean> puedeTransportar(
            @PathVariable String dominio,
            @RequestParam Double peso,
            @RequestParam Double volumen) {
        return ResponseEntity.ok(camionService.puedeTransportar(dominio, peso, volumen));
    }
}
