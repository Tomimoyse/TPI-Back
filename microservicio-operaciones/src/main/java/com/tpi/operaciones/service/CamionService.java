package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Camion;
import com.tpi.operaciones.repository.CamionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CamionService {
    
    @Autowired
    private CamionRepository camionRepository;
    
    public List<Camion> findAll() {
        return camionRepository.findAll();
    }
    
    public Optional<Camion> findById(String dominio) {
        return camionRepository.findById(dominio);
    }
    
    public Camion save(Camion camion) {
        return camionRepository.save(camion);
    }
    
    public void delete(String dominio) {
        camionRepository.deleteById(dominio);
    }
    
    // Buscar camiones disponibles
    public List<Camion> findDisponibles() {
        return camionRepository.findByDisponible(true);
    }
    
    // Buscar camiones aptos para un contenedor
    public List<Camion> findAptosParaContenedor(Double peso, Double volumen) {
        return camionRepository.findByCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(peso, volumen);
    }
    
    // Validar que un camión pueda transportar un contenedor
    public boolean puedeTransportar(String dominio, Double peso, Double volumen) {
        Optional<Camion> camion = findById(dominio);
        if (camion.isEmpty()) {
            return false;
        }
        
        Camion c = camion.get();
        return c.getCapacidadPeso() >= peso && c.getCapacidadVolumen() >= volumen;
    }
    
    // Marcar camión como ocupado
    public void marcarOcupado(String dominio) {
        Optional<Camion> camion = findById(dominio);
        if (camion.isPresent()) {
            Camion c = camion.get();
            c.setDisponible(false);
            save(c);
        }
    }
    
    // Marcar camión como disponible
    public void marcarDisponible(String dominio) {
        Optional<Camion> camion = findById(dominio);
        if (camion.isPresent()) {
            Camion c = camion.get();
            c.setDisponible(true);
            save(c);
        }
    }
}
