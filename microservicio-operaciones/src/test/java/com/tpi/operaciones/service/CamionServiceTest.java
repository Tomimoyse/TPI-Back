package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Camion;
import com.tpi.operaciones.repository.CamionRepository;
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
 * Tests para CamionService
 * RF10: Registrar y actualizar camiones
 * RF11: Validar que un camión no supere su capacidad máxima
 */
@ExtendWith(MockitoExtension.class)
class CamionServiceTest {

    @Mock
    private CamionRepository camionRepository;

    @InjectMocks
    private CamionService camionService;

    private Camion camionTest;

    @BeforeEach
    void setUp() {
        camionTest = new Camion();
        camionTest.setDominio("ABC123");
        camionTest.setNombreTransportista("Carlos Pérez");
        camionTest.setTelefono("+54911234567");
        camionTest.setCapacidadPeso(10000.0);
        camionTest.setCapacidadVolumen(50.0);
        camionTest.setDisponible(true);
        camionTest.setConsumoPorKm(0.35);
        camionTest.setCostoBasePorKm(50.0);
    }

    /**
     * RF10: Test para crear camión
     */
    @Test
    void testCrearCamion() {
        // Given
        when(camionRepository.save(any(Camion.class))).thenReturn(camionTest);

        // When
        Camion resultado = camionService.save(camionTest);

        // Then
        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getDominio());
        assertEquals("Carlos Pérez", resultado.getNombreTransportista());
        assertEquals(10000.0, resultado.getCapacidadPeso());
        assertEquals(50.0, resultado.getCapacidadVolumen());
        assertTrue(resultado.getDisponible());
        verify(camionRepository).save(any(Camion.class));
    }

    /**
     * RF10: Test para buscar camión por dominio
     */
    @Test
    void testBuscarCamionPorDominio() {
        // Given
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));

        // When
        Optional<Camion> resultado = camionService.findById("ABC123");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("ABC123", resultado.get().getDominio());
        verify(camionRepository).findById("ABC123");
    }

    /**
     * RF10: Test para listar todos los camiones
     */
    @Test
    void testListarTodosLosCamiones() {
        // Given
        Camion camion2 = new Camion();
        camion2.setDominio("XYZ789");
        camion2.setNombreTransportista("Laura Martínez");
        camion2.setCapacidadPeso(15000.0);
        camion2.setCapacidadVolumen(80.0);
        camion2.setDisponible(true);
        
        List<Camion> camiones = Arrays.asList(camionTest, camion2);
        when(camionRepository.findAll()).thenReturn(camiones);

        // When
        List<Camion> resultado = camionService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(camionRepository).findAll();
    }

    /**
     * RF10: Test para actualizar camión
     */
    @Test
    void testActualizarCamion() {
        // Given
        camionTest.setTelefono("+54911999888");
        when(camionRepository.save(any(Camion.class))).thenReturn(camionTest);

        // When
        Camion resultado = camionService.save(camionTest);

        // Then
        assertNotNull(resultado);
        assertEquals("+54911999888", resultado.getTelefono());
        verify(camionRepository).save(any(Camion.class));
    }

    /**
     * RF10: Test para eliminar camión
     */
    @Test
    void testEliminarCamion() {
        // Given
        String dominio = "ABC123";
        doNothing().when(camionRepository).deleteById(dominio);

        // When
        camionService.delete(dominio);

        // Then
        verify(camionRepository).deleteById(dominio);
    }

    /**
     * Test para buscar camiones disponibles
     */
    @Test
    void testBuscarCamionesDisponibles() {
        // Given
        List<Camion> camionesDisponibles = Arrays.asList(camionTest);
        when(camionRepository.findByDisponible(true)).thenReturn(camionesDisponibles);

        // When
        List<Camion> resultado = camionService.findDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDisponible());
        verify(camionRepository).findByDisponible(true);
    }

    /**
     * RF11: Test para validar capacidad de camión - APTO
     */
    @Test
    void testPuedeTransportarContenedorApto() {
        // Given
        Double peso = 8000.0;
        Double volumen = 40.0;
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));

        // When
        boolean resultado = camionService.puedeTransportar("ABC123", peso, volumen);

        // Then
        assertTrue(resultado);
        verify(camionRepository).findById("ABC123");
    }

    /**
     * RF11: Test para validar capacidad de camión - NO APTO por peso
     */
    @Test
    void testPuedeTransportarContenedorNoAptoPorPeso() {
        // Given
        Double peso = 12000.0; // Excede capacidad de 10000
        Double volumen = 40.0;
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));

        // When
        boolean resultado = camionService.puedeTransportar("ABC123", peso, volumen);

        // Then
        assertFalse(resultado);
        verify(camionRepository).findById("ABC123");
    }

    /**
     * RF11: Test para validar capacidad de camión - NO APTO por volumen
     */
    @Test
    void testPuedeTransportarContenedorNoAptoPorVolumen() {
        // Given
        Double peso = 8000.0;
        Double volumen = 60.0; // Excede capacidad de 50.0
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));

        // When
        boolean resultado = camionService.puedeTransportar("ABC123", peso, volumen);

        // Then
        assertFalse(resultado);
        verify(camionRepository).findById("ABC123");
    }

    /**
     * RF11: Test para validar capacidad de camión inexistente
     */
    @Test
    void testPuedeTransportarCamionInexistente() {
        // Given
        when(camionRepository.findById("NOEXISTE")).thenReturn(Optional.empty());

        // When
        boolean resultado = camionService.puedeTransportar("NOEXISTE", 5000.0, 30.0);

        // Then
        assertFalse(resultado);
        verify(camionRepository).findById("NOEXISTE");
    }

    /**
     * Test para buscar camiones aptos para un contenedor
     */
    @Test
    void testBuscarCamionesAptosParaContenedor() {
        // Given
        Double peso = 8000.0;
        Double volumen = 40.0;
        List<Camion> camionesAptos = Arrays.asList(camionTest);
        when(camionRepository.findByCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(peso, volumen))
            .thenReturn(camionesAptos);

        // When
        List<Camion> resultado = camionService.findAptosParaContenedor(peso, volumen);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getCapacidadPeso() >= peso);
        assertTrue(resultado.get(0).getCapacidadVolumen() >= volumen);
        verify(camionRepository).findByCapacidadPesoGreaterThanEqualAndCapacidadVolumenGreaterThanEqual(peso, volumen);
    }

    /**
     * Test para marcar camión como ocupado
     */
    @Test
    void testMarcarCamionComoOcupado() {
        // Given
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));
        when(camionRepository.save(any(Camion.class))).thenReturn(camionTest);

        // When
        camionService.marcarOcupado("ABC123");

        // Then
        verify(camionRepository).findById("ABC123");
        verify(camionRepository).save(any(Camion.class));
    }

    /**
     * Test para marcar camión como disponible
     */
    @Test
    void testMarcarCamionComoDisponible() {
        // Given
        camionTest.setDisponible(false);
        when(camionRepository.findById("ABC123")).thenReturn(Optional.of(camionTest));
        when(camionRepository.save(any(Camion.class))).thenReturn(camionTest);

        // When
        camionService.marcarDisponible("ABC123");

        // Then
        verify(camionRepository).findById("ABC123");
        verify(camionRepository).save(any(Camion.class));
    }
}
