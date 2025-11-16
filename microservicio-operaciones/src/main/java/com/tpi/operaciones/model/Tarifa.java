package com.tpi.operaciones.model;

import jakarta.persistence.*;

@Entity
public class Tarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private Double precioPorKm;
    // getters y setters
}

