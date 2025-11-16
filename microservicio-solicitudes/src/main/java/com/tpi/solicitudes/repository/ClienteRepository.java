package com.tpi.solicitudes.repository;

import com.tpi.solicitudes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}