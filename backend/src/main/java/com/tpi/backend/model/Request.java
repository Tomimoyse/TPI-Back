package com.tpi.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double estimatedCost;
    private Integer estimatedTimeMinutes;
    private Double finalCost;
    private Integer realTimeMinutes;
    private String state;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private Container container;

    public Request() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }
    public Double getFinalCost() { return finalCost; }
    public void setFinalCost(Double finalCost) { this.finalCost = finalCost; }
    public Integer getRealTimeMinutes() { return realTimeMinutes; }
    public void setRealTimeMinutes(Integer realTimeMinutes) { this.realTimeMinutes = realTimeMinutes; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Container getContainer() { return container; }
    public void setContainer(Container container) { this.container = container; }
}

