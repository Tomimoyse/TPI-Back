package com.tpi.operaciones.model;

import jakarta.persistence.*;

@Entity
public class Camion {
    @Id
    private String dominio; // patente u otro identificador
    
    @Column(nullable = false)
    private String nombreTransportista;
    
    private String telefono;
    
    @Column(nullable = false)
    private Double capacidadPeso;
    
    @Column(nullable = false)
    private Double capacidadVolumen;
    
    // Disponibilidad del camión
    @Column(nullable = false)
    private Boolean disponible = true;
    
    // Consumo de combustible por km (litros/km)
    @Column(nullable = false)
    private Double consumoPorKm;
    
    // Costo base por kilómetro
    @Column(nullable = false)
    private Double costoBasePorKm;
    
    // Getters y setters
    public String getDominio() {
        return dominio;
    }
    
    public void setDominio(String dominio) {
        this.dominio = dominio;
    }
    
    public String getNombreTransportista() {
        return nombreTransportista;
    }
    
    public void setNombreTransportista(String nombreTransportista) {
        this.nombreTransportista = nombreTransportista;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public Double getCapacidadPeso() {
        return capacidadPeso;
    }
    
    public void setCapacidadPeso(Double capacidadPeso) {
        this.capacidadPeso = capacidadPeso;
    }
    
    public Double getCapacidadVolumen() {
        return capacidadVolumen;
    }
    
    public void setCapacidadVolumen(Double capacidadVolumen) {
        this.capacidadVolumen = capacidadVolumen;
    }
    
    public Boolean getDisponible() {
        return disponible;
    }
    
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
    
    public Double getConsumoPorKm() {
        return consumoPorKm;
    }
    
    public void setConsumoPorKm(Double consumoPorKm) {
        this.consumoPorKm = consumoPorKm;
    }
    
    public Double getCostoBasePorKm() {
        return costoBasePorKm;
    }
    
    public void setCostoBasePorKm(Double costoBasePorKm) {
        this.costoBasePorKm = costoBasePorKm;
    }
}
