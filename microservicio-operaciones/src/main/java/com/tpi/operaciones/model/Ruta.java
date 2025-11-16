package com.tpi.operaciones.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    // ID de la solicitud asociada (en microservicio-solicitudes)
    private Long solicitudId;
    
    // Cantidad de tramos y depósitos
    private Integer cantidadTramos;
    private Integer cantidadDepositos;
    
    // Distancia total de la ruta
    private Double distanciaTotal;
    
    // Costo y tiempo estimado
    private Double costoEstimado;
    private Double costoReal;
    private Integer tiempoEstimadoHoras;
    private Integer tiempoRealHoras;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ruta_id")
    private List<Tramo> tramos = new ArrayList<>();

    // Getters y Setters
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

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public Integer getCantidadTramos() {
        return cantidadTramos;
    }

    public void setCantidadTramos(Integer cantidadTramos) {
        this.cantidadTramos = cantidadTramos;
    }

    public Integer getCantidadDepositos() {
        return cantidadDepositos;
    }

    public void setCantidadDepositos(Integer cantidadDepositos) {
        this.cantidadDepositos = cantidadDepositos;
    }

    public Double getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(Double distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }

    public Double getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(Double costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

    public Double getCostoReal() {
        return costoReal;
    }

    public void setCostoReal(Double costoReal) {
        this.costoReal = costoReal;
    }

    public Integer getTiempoEstimadoHoras() {
        return tiempoEstimadoHoras;
    }

    public void setTiempoEstimadoHoras(Integer tiempoEstimadoHoras) {
        this.tiempoEstimadoHoras = tiempoEstimadoHoras;
    }

    public Integer getTiempoRealHoras() {
        return tiempoRealHoras;
    }

    public void setTiempoRealHoras(Integer tiempoRealHoras) {
        this.tiempoRealHoras = tiempoRealHoras;
    }

    public List<Tramo> getTramos() {
        return tramos;
    }

    public void setTramos(List<Tramo> tramos) {
        this.tramos = tramos;
    }
}

