package com.tpi.operaciones.model;

import jakarta.persistence.*;

@Entity
public class Deposito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String ubicacion;
    // getters y setters
}

