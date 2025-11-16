package com.tpi.solicitudes.repository;

import com.tpi.solicitudes.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {}

