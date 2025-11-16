package com.tpi.operaciones.model;

import jakarta.persistence.*;

@Entity
public class Deposito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String direccion;
    
    // Coordenadas geográficas
    @Column(nullable = false)
    private Double latitud;
    
    @Column(nullable = false)
    private Double longitud;
    
    // Costo de estadía diaria
    @Column(nullable = false)
    private Double costoEstadiaDiaria;
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public Double getLatitud() {
        return latitud;
    }
    
    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
    
    public Double getLongitud() {
        return longitud;
    }
    
    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
    
    public Double getCostoEstadiaDiaria() {
        return costoEstadiaDiaria;
    }
    
    public void setCostoEstadiaDiaria(Double costoEstadiaDiaria) {
        this.costoEstadiaDiaria = costoEstadiaDiaria;
    }
}

