package com.tpi.solicitudes.controller;

import com.tpi.solicitudes.model.Solicitud;
import com.tpi.solicitudes.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes/solicitudes")
public class SolicitudController {
    @Autowired
    private SolicitudService solicitudService;

    @GetMapping
    public List<Solicitud> getAll() {
        return solicitudService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> getById(@PathVariable Long id) {
        return solicitudService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Solicitud create(@RequestBody Solicitud solicitud) {
        return solicitudService.save(solicitud);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        solicitudService.delete(id);
    }
}

