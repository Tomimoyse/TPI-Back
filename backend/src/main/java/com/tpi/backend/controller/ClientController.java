package com.tpi.backend.controller;

import com.tpi.backend.model.Client;
import com.tpi.backend.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientRepository repository;

    public ClientController(ClientRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Client> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> get(@PathVariable Long id) {
        Optional<Client> c = repository.findById(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Client create(@RequestBody Client client) {
        return repository.save(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        return repository.findById(id).map(existing -> {
            existing.setName(client.getName());
            existing.setLastName(client.getLastName());
            existing.setDni(client.getDni());
            existing.setPhone(client.getPhone());
            existing.setEmail(client.getEmail());
            existing.setAddress(client.getAddress());
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

