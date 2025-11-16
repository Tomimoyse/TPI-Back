package com.tpi.backend.controller;

import com.tpi.backend.model.Truck;
import com.tpi.backend.repository.TruckRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trucks")
public class TruckController {
    private final TruckRepository repository;

    public TruckController(TruckRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Truck> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Truck> get(@PathVariable String id) {
        Optional<Truck> t = repository.findById(id);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Truck create(@RequestBody Truck truck) {
        return repository.save(truck);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Truck> update(@PathVariable String id, @RequestBody Truck truck) {
        return repository.findById(id).map(existing -> {
            existing.setDriverName(truck.getDriverName());
            existing.setPhone(truck.getPhone());
            existing.setCapacityWeight(truck.getCapacityWeight());
            existing.setCapacityVolume(truck.getCapacityVolume());
            existing.setAvailable(truck.getAvailable());
            existing.setConsumptionPerKm(truck.getConsumptionPerKm());
            existing.setBaseCostPerKm(truck.getBaseCostPerKm());
            repository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return repository.findById(id).map(existing -> {
            repository.delete(existing);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

