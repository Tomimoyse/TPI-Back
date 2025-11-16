package com.tpi.backend.controller;

import com.tpi.backend.model.Request;
import com.tpi.backend.model.Client;
import com.tpi.backend.model.Container;
import com.tpi.backend.repository.RequestRepository;
import com.tpi.backend.repository.ClientRepository;
import com.tpi.backend.repository.ContainerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestRepository repository;
    private final ClientRepository clientRepo;
    private final ContainerRepository containerRepo;

    public RequestController(RequestRepository repository, ClientRepository clientRepo, ContainerRepository containerRepo) {
        this.repository = repository;
        this.clientRepo = clientRepo;
        this.containerRepo = containerRepo;
    }

    @GetMapping
    public List<Request> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> get(@PathVariable Long id) {
        Optional<Request> r = repository.findById(id);
        return r.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Request> create(@RequestBody Request request) {
        // if client provided with id, attach existing client
        if (request.getClient() != null && request.getClient().getId() != null) {
            Optional<Client> c = clientRepo.findById(request.getClient().getId());
            c.ifPresent(request::setClient);
        }
        // if container provided with id, attach existing container
        if (request.getContainer() != null && request.getContainer().getId() != null) {
            Optional<Container> ct = containerRepo.findById(request.getContainer().getId());
            ct.ifPresent(request::setContainer);
        }
        Request saved = repository.save(request);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> update(@PathVariable Long id, @RequestBody Request request) {
        return repository.findById(id).map(existing -> {
            existing.setEstimatedCost(request.getEstimatedCost());
            existing.setEstimatedTimeMinutes(request.getEstimatedTimeMinutes());
            existing.setFinalCost(request.getFinalCost());
            existing.setRealTimeMinutes(request.getRealTimeMinutes());
            existing.setState(request.getState());
            // client/container updates optional
            if (request.getClient() != null && request.getClient().getId() != null) {
                clientRepo.findById(request.getClient().getId()).ifPresent(existing::setClient);
            }
            if (request.getContainer() != null && request.getContainer().getId() != null) {
                containerRepo.findById(request.getContainer().getId()).ifPresent(existing::setContainer);
            }
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

