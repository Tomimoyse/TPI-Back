package com.tpi.operaciones.model;

import jakarta.persistence.*;

@Entity
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String descripcion;
    
    // Costo base por kilómetro (según volumen del contenedor)
    private Double costoBaseKm;
    
    // Valor del litro de combustible
    private Double valorLitroCombustible;
    
    // Rango de peso (kg)
    private Double pesoMinimo;
    private Double pesoMaximo;
    
    // Rango de volumen (m3)
    private Double volumenMinimo;
    private Double volumenMaximo;
    
    // Cargo de gestión fijo
    private Double cargoGestionFijo;
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Double getCostoBaseKm() {
        return costoBaseKm;
    }
    
    public void setCostoBaseKm(Double costoBaseKm) {
        this.costoBaseKm = costoBaseKm;
    }
    
    public Double getValorLitroCombustible() {
        return valorLitroCombustible;
    }
    
    public void setValorLitroCombustible(Double valorLitroCombustible) {
        this.valorLitroCombustible = valorLitroCombustible;
    }
    
    public Double getPesoMinimo() {
        return pesoMinimo;
    }
    
    public void setPesoMinimo(Double pesoMinimo) {
        this.pesoMinimo = pesoMinimo;
    }
    
    public Double getPesoMaximo() {
        return pesoMaximo;
    }
    
    public void setPesoMaximo(Double pesoMaximo) {
        this.pesoMaximo = pesoMaximo;
    }
    
    public Double getVolumenMinimo() {
        return volumenMinimo;
    }
    
    public void setVolumenMinimo(Double volumenMinimo) {
        this.volumenMinimo = volumenMinimo;
    }
    
    public Double getVolumenMaximo() {
        return volumenMaximo;
    }
    
    public void setVolumenMaximo(Double volumenMaximo) {
        this.volumenMaximo = volumenMaximo;
    }
    
    public Double getCargoGestionFijo() {
        return cargoGestionFijo;
    }
    
    public void setCargoGestionFijo(Double cargoGestionFijo) {
        this.cargoGestionFijo = cargoGestionFijo;
    }
}

