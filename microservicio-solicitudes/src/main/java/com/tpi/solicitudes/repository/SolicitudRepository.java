package com.tpi.solicitudes.repository;

import com.tpi.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    
    // Buscar solicitudes por estado
    List<Solicitud> findByEstado(String estado);
    
    // Buscar solicitudes por cliente
    List<Solicitud> findByClienteId(Long clienteId);
    
    // Buscar solicitudes por contenedor
    List<Solicitud> findByContenedorId(Long contenedorId);
}

