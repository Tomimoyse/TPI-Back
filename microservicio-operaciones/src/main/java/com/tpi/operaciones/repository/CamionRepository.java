package com.tpi.operaciones.repository;

import com.tpi.operaciones.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
    
    // Buscar camiones disponibles
    List<Camion> findByDisponible(Boolean disponible);
    
    // Buscar camiones por capacidad mínima
    List<Camion> findByCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(
        Double capacidadPeso, Double capacidadVolumen);
}
