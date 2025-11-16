package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.service.TramoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tramos")
public class TramoController {

    @Autowired
    private TramoService tramoService;

    @GetMapping
    public List<Tramo> listarTramos() {
        return tramoService.listarTramos();
    }

    @PostMapping
    public Tramo crearTramo(@RequestBody Tramo tramo) {
        return tramoService.crearTramo(tramo);
    }

    @GetMapping("/distancia")
    public ResponseEntity<?> calcularDistancia(
            @RequestParam String origen,
            @RequestParam String destino) {
        
        try {
            Double distancia = tramoService.calcularDistancia(origen, destino);
            return ResponseEntity.ok(distancia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al calcular distancia: " + e.getMessage());
        }
    }
}
