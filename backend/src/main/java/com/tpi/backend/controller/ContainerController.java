package com.tpi.backend.controller;

import com.tpi.backend.model.Container;
import com.tpi.backend.repository.ContainerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/containers")
public class ContainerController {
    private final ContainerRepository repository;

    public ContainerController(ContainerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Container> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Container> get(@PathVariable Long id) {
        Optional<Container> c = repository.findById(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Container create(@RequestBody Container container) {
        return repository.save(container);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Container> update(@PathVariable Long id, @RequestBody Container container) {
        return repository.findById(id).map(existing -> {
            existing.setWeight(container.getWeight());
            existing.setVolume(container.getVolume());
            existing.setStatus(container.getStatus());
            existing.setClient(container.getClient());
            repository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id).map(existing -> {
            repository.delete(existing);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

