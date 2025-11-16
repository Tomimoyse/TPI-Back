package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Camion;
import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.model.Tarifa;
import com.tpi.operaciones.model.Tramo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CostoService {
    
    @Autowired
    private TarifaService tarifaService;
    
    @Autowired
    private CamionService camionService;
    
    @Autowired
    private DepositoService depositoService;
    
    /**
     * Calcula el costo estimado de un tramo basado en distancia y características del contenedor
     * Utiliza valores promedio de camiones aptos para el contenedor
     */
    public Double calcularCostoEstimadoTramo(Double distanciaKm, Double peso, Double volumen) {
        // Obtener camiones aptos
        List<Camion> camionesAptos = camionService.findAptosParaContenedor(peso, volumen);
        
        if (camionesAptos.isEmpty()) {
            throw new RuntimeException("No hay camiones disponibles para este contenedor");
        }
        
        // Calcular promedios
        double consumoPromedio = camionesAptos.stream()
            .mapToDouble(Camion::getConsumoPorKm)
            .average()
            .orElse(0.0);
        
        double costoBasePromedio = camionesAptos.stream()
            .mapToDouble(Camion::getCostoBasePorKm)
            .average()
            .orElse(0.0);
        
        // Obtener tarifa según peso y volumen
        List<Tarifa> tarifas = tarifaService.findAll();
        Tarifa tarifaAplicable = tarifas.stream()
            .filter(t -> 
                (t.getPesoMinimo() == null || peso >= t.getPesoMinimo()) &&
                (t.getPesoMaximo() == null || peso <= t.getPesoMaximo()) &&
                (t.getVolumenMinimo() == null || volumen >= t.getVolumenMinimo()) &&
                (t.getVolumenMaximo() == null || volumen <= t.getVolumenMaximo())
            )
            .findFirst()
            .orElse(tarifas.isEmpty() ? null : tarifas.get(0));
        
        double valorCombustible = tarifaAplicable != null ? 
            tarifaAplicable.getValorLitroCombustible() : 150.0; // Valor por defecto
        
        double cargoGestion = tarifaAplicable != null ? 
            tarifaAplicable.getCargoGestionFijo() : 1000.0; // Valor por defecto
        
        // Costo = cargo gestión + (costo base por km * distancia) + (consumo * distancia * valor combustible)
        double costoBase = costoBasePromedio * distanciaKm;
        double costoCombustible = consumoPromedio * distanciaKm * valorCombustible;
        
        return cargoGestion + costoBase + costoCombustible;
    }
    
    /**
     * Calcula el costo real de un tramo con un camión específico
     */
    public Double calcularCostoRealTramo(Tramo tramo, String camionDominio) {
        Camion camion = camionService.findById(camionDominio)
            .orElseThrow(() -> new RuntimeException("Camión no encontrado"));
        
        if (tramo.getDistancia() == null) {
            throw new RuntimeException("La distancia del tramo no está definida");
        }
        
        List<Tarifa> tarifas = tarifaService.findAll();
        Tarifa tarifaAplicable = tarifas.isEmpty() ? null : tarifas.get(0);
        
        double valorCombustible = tarifaAplicable != null ? 
            tarifaAplicable.getValorLitroCombustible() : 150.0;
        
        double cargoGestion = tarifaAplicable != null ? 
            tarifaAplicable.getCargoGestionFijo() : 1000.0;
        
        // Costo = cargo gestión + (costo base camión * distancia) + (consumo camión * distancia * valor combustible)
        double costoBase = camion.getCostoBasePorKm() * tramo.getDistancia();
        double costoCombustible = camion.getConsumoPorKm() * tramo.getDistancia() * valorCombustible;
        
        return cargoGestion + costoBase + costoCombustible;
    }
    
    /**
     * Calcula el costo de estadía en un depósito
     */
    public Double calcularCostoEstadia(Long depositoId, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        Deposito deposito = depositoService.findById(depositoId)
            .orElseThrow(() -> new RuntimeException("Depósito no encontrado"));
        
        if (fechaEntrada == null || fechaSalida == null) {
            return 0.0;
        }
        
        // Calcular días de estadía (redondeando hacia arriba)
        Duration duracion = Duration.between(fechaEntrada, fechaSalida);
        long horas = duracion.toHours();
        long dias = (horas / 24) + (horas % 24 > 0 ? 1 : 0);
        
        return deposito.getCostoEstadiaDiaria() * dias;
    }
    
    /**
     * Calcula el tiempo estimado de viaje en horas basado en distancia
     * Asume una velocidad promedio de 60 km/h
     */
    public Integer calcularTiempoEstimado(Double distanciaKm) {
        double velocidadPromedio = 60.0; // km/h
        return (int) Math.ceil(distanciaKm / velocidadPromedio);
    }
}
