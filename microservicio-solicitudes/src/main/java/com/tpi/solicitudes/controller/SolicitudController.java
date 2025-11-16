package com.tpi.solicitudes.controller;

import com.tpi.solicitudes.model.Cliente;
import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.model.Solicitud;
import com.tpi.solicitudes.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    
    /**
     * Requerimiento 1: Registrar una nueva solicitud de transporte con cliente y contenedor
     */
    @PostMapping("/completa")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Solicitud> crearSolicitudCompleta(@RequestBody Map<String, Object> request) {
        try {
            // Extraer datos de la solicitud
            Solicitud solicitud = new Solicitud();
            solicitud.setDescripcion((String) request.get("descripcion"));
            solicitud.setDireccionOrigen((String) request.get("direccionOrigen"));
            solicitud.setDireccionDestino((String) request.get("direccionDestino"));
            solicitud.setLatitudOrigen((Double) request.get("latitudOrigen"));
            solicitud.setLongitudOrigen((Double) request.get("longitudOrigen"));
            solicitud.setLatitudDestino((Double) request.get("latitudDestino"));
            solicitud.setLongitudDestino((Double) request.get("longitudDestino"));
            
            // Extraer datos del cliente
            Map<String, Object> clienteData = (Map<String, Object>) request.get("cliente");
            Cliente cliente = new Cliente();
            cliente.setNombre((String) clienteData.get("nombre"));
            cliente.setEmail((String) clienteData.get("email"));
            cliente.setTelefono((String) clienteData.get("telefono"));
            cliente.setDireccion((String) clienteData.get("direccion"));
            cliente.setDocumento((String) clienteData.get("documento"));
            
            // Extraer datos del contenedor
            Map<String, Object> contenedorData = (Map<String, Object>) request.get("contenedor");
            Contenedor contenedor = new Contenedor();
            contenedor.setIdentificacion((String) contenedorData.get("identificacion"));
            contenedor.setTipo((String) contenedorData.get("tipo"));
            contenedor.setPeso(((Number) contenedorData.get("peso")).doubleValue());
            contenedor.setVolumen(((Number) contenedorData.get("volumen")).doubleValue());
            contenedor.setEstado("pendiente");
            
            Solicitud solicitudCreada = solicitudService.crearSolicitudCompleta(solicitud, cliente, contenedor);
            return ResponseEntity.ok(solicitudCreada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Requerimiento 2: Consultar el estado del transporte de un contenedor (Cliente)
     */
    @GetMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> consultarEstado(@PathVariable Long id) {
        return solicitudService.findById(id)
                .map(solicitud -> {
                    Map<String, Object> estado = Map.of(
                        "solicitudId", solicitud.getId(),
                        "estado", solicitud.getEstado(),
                        "contenedorId", solicitud.getContenedor().getId(),
                        "contenedorIdentificacion", solicitud.getContenedor().getIdentificacion(),
                        "contenedorEstado", solicitud.getContenedor().getEstado() != null ? solicitud.getContenedor().getEstado() : "pendiente",
                        "direccionOrigen", solicitud.getDireccionOrigen(),
                        "direccionDestino", solicitud.getDireccionDestino(),
                        "costoEstimado", solicitud.getCostoEstimado(),
                        "tiempoEstimadoHoras", solicitud.getTiempoEstimadoHoras(),
                        "rutaId", solicitud.getRutaId()
                    );
                    return ResponseEntity.ok(estado);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Requerimiento 5: Consultar todos los contenedores pendientes con filtros
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public List<Solicitud> consultarPendientes(@RequestParam(required = false) String estado) {
        if (estado != null) {
            return solicitudService.findByEstado(estado);
        }
        // Por defecto, devolver las que no están entregadas
        return solicitudService.findAll().stream()
                .filter(s -> !"entregada".equals(s.getEstado()))
                .toList();
    }
    
    /**
     * Requerimiento 4: Asignar una ruta a la solicitud
     */
    @PutMapping("/{id}/asignar-ruta")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Solicitud> asignarRuta(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long rutaId = ((Number) request.get("rutaId")).longValue();
            Double costoEstimado = ((Number) request.get("costoEstimado")).doubleValue();
            Integer tiempoEstimadoHoras = ((Number) request.get("tiempoEstimadoHoras")).intValue();
            
            Solicitud solicitud = solicitudService.asignarRuta(id, rutaId, costoEstimado, tiempoEstimadoHoras);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Actualizar estado de una solicitud
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR') or hasRole('TRANSPORTISTA')")
    public ResponseEntity<Solicitud> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String nuevoEstado = request.get("estado");
            Solicitud solicitud = solicitudService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Requerimiento 9: Finalizar solicitud con costo y tiempo real
     */
    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Solicitud> finalizarSolicitud(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Double costoFinal = ((Number) request.get("costoFinal")).doubleValue();
            Integer tiempoRealHoras = ((Number) request.get("tiempoRealHoras")).intValue();
            
            Solicitud solicitud = solicitudService.finalizarSolicitud(id, costoFinal, tiempoRealHoras);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        solicitudService.delete(id);
    }
}

