package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Ruta;
import com.tpi.operaciones.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/operaciones/rutas")
public class RutaController {
    @Autowired
    private RutaService rutaService;

    @GetMapping
    public List<Ruta> getAll() {
        return rutaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> getById(@PathVariable Long id) {
        return rutaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ruta create(@RequestBody Ruta ruta) {
        return rutaService.save(ruta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rutaService.delete(id);
    }

    @PostMapping("/{id}/asignar-tramos")
    public Ruta asignarTramos(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> tramosIds = body.get("tramosIds");
        return rutaService.asignarTramos(id, tramosIds);
    }

    @GetMapping("/{id}/tiene-tramos")
    public boolean tieneTramos(@PathVariable Long id) {
        return rutaService.tieneTramos(id);
    }
}
