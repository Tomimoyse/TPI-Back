package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Ruta;
import com.tpi.operaciones.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public Ruta create(@RequestBody Ruta ruta) {
        return rutaService.save(ruta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        rutaService.delete(id);
    }

    @PostMapping("/{id}/asignar-tramos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public Ruta asignarTramos(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> tramosIds = body.get("tramosIds");
        return rutaService.asignarTramos(id, tramosIds);
    }

    @GetMapping("/{id}/tiene-tramos")
    public boolean tieneTramos(@PathVariable Long id) {
        return rutaService.tieneTramos(id);
    }
    
    /**
     * Requerimiento 3: Consultar rutas tentativas con todos los tramos sugeridos y el tiempo y costo estimados
     */
    @PostMapping("/calcular-tentativa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Ruta> calcularRutaTentativa(@RequestBody Map<String, Object> request) {
        try {
            Long solicitudId = request.get("solicitudId") != null ? 
                ((Number) request.get("solicitudId")).longValue() : null;
            
            Double latOrigen = ((Number) request.get("latitudOrigen")).doubleValue();
            Double lonOrigen = ((Number) request.get("longitudOrigen")).doubleValue();
            String dirOrigen = (String) request.get("direccionOrigen");
            
            Double latDestino = ((Number) request.get("latitudDestino")).doubleValue();
            Double lonDestino = ((Number) request.get("longitudDestino")).doubleValue();
            String dirDestino = (String) request.get("direccionDestino");
            
            Double peso = ((Number) request.get("peso")).doubleValue();
            Double volumen = ((Number) request.get("volumen")).doubleValue();
            
            List<Long> depositoIds = request.get("depositoIds") != null ?
                ((List<Number>) request.get("depositoIds")).stream()
                    .map(Number::longValue)
                    .toList() : null;
            
            Ruta ruta = rutaService.calcularRutaTentativa(
                solicitudId, latOrigen, lonOrigen, dirOrigen,
                latDestino, lonDestino, dirDestino,
                depositoIds, peso, volumen
            );
            
            return ResponseEntity.ok(ruta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Calcula el costo y tiempo real de una ruta
     */
    @PutMapping("/{id}/calcular-real")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Ruta> calcularCostoTiempoReal(@PathVariable Long id) {
        try {
            Ruta ruta = rutaService.calcularCostoTiempoReal(id);
            return ResponseEntity.ok(ruta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
