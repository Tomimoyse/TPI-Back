package com.tpi.solicitudes.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private LocalDate fecha;
    @ManyToOne
    private Cliente cliente;
    @ManyToOne
    private Contenedor contenedor;

    // Getters y Setters
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Contenedor getContenedor() {
        return contenedor;
    }

    public void setContenedor(Contenedor contenedor) {
        this.contenedor = contenedor;
    }
}



