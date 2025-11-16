package com.tpi.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trucks")
public class Truck {
    @Id
    private String domain; // patente u otro identificador

    private String driverName;
    private String phone;
    private Double capacityWeight;
    private Double capacityVolume;
    private Boolean available = true;
    private Double consumptionPerKm;
    private Double baseCostPerKm;

    public Truck() {}

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getCapacityWeight() { return capacityWeight; }
    public void setCapacityWeight(Double capacityWeight) { this.capacityWeight = capacityWeight; }
    public Double getCapacityVolume() { return capacityVolume; }
    public void setCapacityVolume(Double capacityVolume) { this.capacityVolume = capacityVolume; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public Double getConsumptionPerKm() { return consumptionPerKm; }
    public void setConsumptionPerKm(Double consumptionPerKm) { this.consumptionPerKm = consumptionPerKm; }
    public Double getBaseCostPerKm() { return baseCostPerKm; }
    public void setBaseCostPerKm(Double baseCostPerKm) { this.baseCostPerKm = baseCostPerKm; }
}

