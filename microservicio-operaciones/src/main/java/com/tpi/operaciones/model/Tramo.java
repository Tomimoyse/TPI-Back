package com.tpi.operaciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Origen y destino (pueden ser direcciones o IDs de depósito)
    private String origen;
    private String destino;
    
    // Coordenadas origen
    private Double latitudOrigen;
    private Double longitudOrigen;
    
    // Coordenadas destino
    private Double latitudDestino;
    private Double longitudDestino;
    
    // Tipo de tramo: origen-deposito, deposito-deposito, deposito-destino, origen-destino
    @Column(nullable = false)
    private String tipoTramo;
    
    // Estado: estimado, asignado, iniciado, finalizado
    @Column(nullable = false)
    private String estado = "estimado";
    
    // Distancia en kilómetros
    private Double distancia;
    
    // Costos
    private Double costoAproximado;
    private Double costoReal;
    
    // Fechas y horas
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private LocalDateTime fechaHoraEstimadaInicio;
    private LocalDateTime fechaHoraEstimadaFin;
    
    // Relación con camión (dominio)
    private String camionDominio;
    
    // ID del depósito si aplica
    private Long depositoId;
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrigen() {
        return origen;
    }
    
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    public String getDestino() {
        return destino;
    }
    
    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    public Double getLatitudOrigen() {
        return latitudOrigen;
    }
    
    public void setLatitudOrigen(Double latitudOrigen) {
        this.latitudOrigen = latitudOrigen;
    }
    
    public Double getLongitudOrigen() {
        return longitudOrigen;
    }
    
    public void setLongitudOrigen(Double longitudOrigen) {
        this.longitudOrigen = longitudOrigen;
    }
    
    public Double getLatitudDestino() {
        return latitudDestino;
    }
    
    public void setLatitudDestino(Double latitudDestino) {
        this.latitudDestino = latitudDestino;
    }
    
    public Double getLongitudDestino() {
        return longitudDestino;
    }
    
    public void setLongitudDestino(Double longitudDestino) {
        this.longitudDestino = longitudDestino;
    }
    
    public String getTipoTramo() {
        return tipoTramo;
    }
    
    public void setTipoTramo(String tipoTramo) {
        this.tipoTramo = tipoTramo;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Double getDistancia() {
        return distancia;
    }
    
    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
    
    public Double getCostoAproximado() {
        return costoAproximado;
    }
    
    public void setCostoAproximado(Double costoAproximado) {
        this.costoAproximado = costoAproximado;
    }
    
    public Double getCostoReal() {
        return costoReal;
    }
    
    public void setCostoReal(Double costoReal) {
        this.costoReal = costoReal;
    }
    
    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
    
    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
    
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }
    
    public LocalDateTime getFechaHoraEstimadaInicio() {
        return fechaHoraEstimadaInicio;
    }
    
    public void setFechaHoraEstimadaInicio(LocalDateTime fechaHoraEstimadaInicio) {
        this.fechaHoraEstimadaInicio = fechaHoraEstimadaInicio;
    }
    
    public LocalDateTime getFechaHoraEstimadaFin() {
        return fechaHoraEstimadaFin;
    }
    
    public void setFechaHoraEstimadaFin(LocalDateTime fechaHoraEstimadaFin) {
        this.fechaHoraEstimadaFin = fechaHoraEstimadaFin;
    }
    
    public String getCamionDominio() {
        return camionDominio;
    }
    
    public void setCamionDominio(String camionDominio) {
        this.camionDominio = camionDominio;
    }
    
    public Long getDepositoId() {
        return depositoId;
    }
    
    public void setDepositoId(Long depositoId) {
        this.depositoId = depositoId;
    }
}