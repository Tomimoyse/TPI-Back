package com.tpi.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "containers")
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double weight;
    private Double volume;
    private String status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Container() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getVolume() { return volume; }
    public void setVolume(Double volume) { this.volume = volume; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}

