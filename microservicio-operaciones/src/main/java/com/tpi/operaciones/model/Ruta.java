package com.tpi.operaciones.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    
    @OneToMany
    private List<Tramo> tramos;

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

    public List<Tramo> getTramos() {
        return tramos;
    }

    public void setTramos(List<Tramo> tramos) {
        this.tramos = tramos;
    }
}

