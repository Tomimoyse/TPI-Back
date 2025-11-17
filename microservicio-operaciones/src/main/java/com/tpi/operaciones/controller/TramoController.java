package com.tpi.operaciones.controller;

import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.service.TramoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/operaciones/tramos")
public class TramoController {

    @Autowired
    private TramoService tramoService;

    @GetMapping
    public List<Tramo> listarTramos() {
        return tramoService.listarTramos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tramo> getById(@PathVariable Long id) {
        return tramoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public Tramo crearTramo(@RequestBody Tramo tramo) {
        return tramoService.crearTramo(tramo);
    }
    
    /**
     * Requerimiento 6: Asignar camión a un tramo de traslado (Operador/Administrador)
     */
    @PutMapping("/{id}/asignar-camion")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Tramo> asignarCamion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String camionDominio = (String) request.get("camionDominio");
            Double peso = ((Number) request.get("peso")).doubleValue();
            Double volumen = ((Number) request.get("volumen")).doubleValue();
            
            Tramo tramo = tramoService.asignarCamion(id, camionDominio, peso, volumen);
            return ResponseEntity.ok(tramo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Requerimiento 7: Iniciar tramo (Transportista)
     */
    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Tramo> iniciarTramo(@PathVariable Long id) {
        try {
            Tramo tramo = tramoService.iniciarTramo(id);
            return ResponseEntity.ok(tramo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Requerimiento 7: Finalizar tramo (Transportista)
     */
    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('TRANSPORTISTA') or hasRole('ADMIN')")
    public ResponseEntity<Tramo> finalizarTramo(@PathVariable Long id) {
        try {
            Tramo tramo = tramoService.finalizarTramo(id);
            return ResponseEntity.ok(tramo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
    
    // SOLO endpoint por coordenadas
    @GetMapping("/distancia-coordenadas")
    public ResponseEntity<Map<String, Object>> calcularDistanciaPorCoordenadas(
            @RequestParam Double lat1,
            @RequestParam Double lon1,
            @RequestParam Double lat2,
            @RequestParam Double lon2) {
        
        Double distancia = tramoService.calcularDistanciaPorCoordenadas(lat1, lon1, lat2, lon2);
        
        return ResponseEntity.ok(Map.of(
            "distancia", distancia,
            "unidad", "km",
            "coordenadas", Map.of(
                "origen", Map.of("lat", lat1, "lon", lon1),
                "destino", Map.of("lat", lat2, "lon", lon2)
            )
        ));
    }
}
