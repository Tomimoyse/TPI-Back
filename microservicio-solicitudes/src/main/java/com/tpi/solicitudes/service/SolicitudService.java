package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Cliente;
import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.model.Solicitud;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {
    
    @Autowired
    private SolicitudRepository solicitudRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ContenedorService contenedorService;

    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }

    public Optional<Solicitud> findById(Long id) {
        return solicitudRepository.findById(id);
    }

    public Solicitud save(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public void delete(Long id) {
        solicitudRepository.deleteById(id);
    }
    
    // Buscar solicitudes por estado
    public List<Solicitud> findByEstado(String estado) {
        return solicitudRepository.findByEstado(estado);
    }
    
    // Buscar solicitudes por cliente
    public List<Solicitud> findByClienteId(Long clienteId) {
        return solicitudRepository.findByClienteId(clienteId);
    }
    
    /**
     * Crea una solicitud completa con cliente y contenedor
     * Si el cliente existe (por email), lo reutiliza
     * Siempre crea un nuevo contenedor con identificación única
     */
    @Transactional
    public Solicitud crearSolicitudCompleta(Solicitud solicitud, Cliente clienteData, Contenedor contenedorData) {
        // Buscar o crear cliente
        Cliente cliente;
        Optional<Cliente> clienteExistente = clienteService.findByEmail(clienteData.getEmail());
        if (clienteExistente.isPresent()) {
            cliente = clienteExistente.get();
        } else {
            cliente = clienteService.save(clienteData);
        }
        
        // Crear contenedor con identificación única
        Contenedor contenedor = contenedorService.save(contenedorData);
        
        // Asignar cliente y contenedor a la solicitud
        solicitud.setCliente(cliente);
        solicitud.setContenedor(contenedor);
        solicitud.setFecha(LocalDate.now());
        
        // Estado inicial
        if (solicitud.getEstado() == null) {
            solicitud.setEstado("borrador");
        }
        
        return save(solicitud);
    }
    
    /**
     * Actualiza el estado de una solicitud
     */
    public Solicitud actualizarEstado(Long id, String nuevoEstado) {
        Solicitud solicitud = findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado(nuevoEstado);
        return save(solicitud);
    }
    
    /**
     * Asigna una ruta a una solicitud
     */
    public Solicitud asignarRuta(Long solicitudId, Long rutaId, Double costoEstimado, Integer tiempoEstimadoHoras) {
        Solicitud solicitud = findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setRutaId(rutaId);
        solicitud.setCostoEstimado(costoEstimado);
        solicitud.setTiempoEstimadoHoras(tiempoEstimadoHoras);
        solicitud.setEstado("programada");
        
        return save(solicitud);
    }
    
    /**
     * Finaliza una solicitud registrando el costo y tiempo real
     */
    public Solicitud finalizarSolicitud(Long solicitudId, Double costoFinal, Integer tiempoRealHoras) {
        Solicitud solicitud = findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setCostoFinal(costoFinal);
        solicitud.setTiempoRealHoras(tiempoRealHoras);
        solicitud.setEstado("entregada");
        
        return save(solicitud);
    }
}

