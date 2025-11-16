package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.repository.ContenedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para ContenedorService
 * Valida operaciones CRUD de contenedores
 */
@ExtendWith(MockitoExtension.class)
class ContenedorServiceTest {

    @Mock
    private ContenedorRepository contenedorRepository;

    @InjectMocks
    private ContenedorService contenedorService;

    private Contenedor contenedorTest;

    @BeforeEach
    void setUp() {
        contenedorTest = new Contenedor();
        contenedorTest.setId(1L);
        contenedorTest.setIdentificacion("CONT-TEST-001");
        contenedorTest.setTipo("40ft Standard");
        contenedorTest.setPeso(8000.0);
        contenedorTest.setVolumen(67.0);
        contenedorTest.setCapacidad(28000.0);
        contenedorTest.setEstado("disponible");
    }

    @Test
    void testCrearContenedor() {
        // Given
        when(contenedorRepository.save(any(Contenedor.class))).thenReturn(contenedorTest);

        // When
        Contenedor resultado = contenedorService.save(contenedorTest);

        // Then
        assertNotNull(resultado);
        assertEquals("CONT-TEST-001", resultado.getIdentificacion());
        assertEquals(8000.0, resultado.getPeso());
        assertEquals(67.0, resultado.getVolumen());
        verify(contenedorRepository).save(any(Contenedor.class));
    }

    @Test
    void testBuscarContenedorPorId() {
        // Given
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(contenedorTest));

        // When
        Optional<Contenedor> resultado = contenedorService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("CONT-TEST-001", resultado.get().getIdentificacion());
        assertEquals("40ft Standard", resultado.get().getTipo());
        verify(contenedorRepository).findById(1L);
    }

    @Test
    void testListarTodosLosContenedores() {
        // Given
        Contenedor contenedor2 = new Contenedor();
        contenedor2.setId(2L);
        contenedor2.setIdentificacion("CONT-TEST-002");
        contenedor2.setPeso(5000.0);
        contenedor2.setVolumen(33.0);
        
        List<Contenedor> contenedores = Arrays.asList(contenedorTest, contenedor2);
        when(contenedorRepository.findAll()).thenReturn(contenedores);

        // When
        List<Contenedor> resultado = contenedorService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(contenedorRepository).findAll();
    }

    @Test
    void testActualizarContenedor() {
        // Given
        contenedorTest.setEstado("en tránsito");
        when(contenedorRepository.save(any(Contenedor.class))).thenReturn(contenedorTest);

        // When
        Contenedor resultado = contenedorService.save(contenedorTest);

        // Then
        assertNotNull(resultado);
        assertEquals("en tránsito", resultado.getEstado());
        verify(contenedorRepository).save(any(Contenedor.class));
    }

    @Test
    void testEliminarContenedor() {
        // Given
        Long contenedorId = 1L;
        doNothing().when(contenedorRepository).deleteById(contenedorId);

        // When
        contenedorService.delete(contenedorId);

        // Then
        verify(contenedorRepository).deleteById(contenedorId);
    }
}
