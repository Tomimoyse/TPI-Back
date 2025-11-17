package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Tarifa;
import com.tpi.operaciones.repository.TarifaRepository;
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
 * Tests para TarifaService
 * RF10: Registrar y actualizar tarifas
 */
@ExtendWith(MockitoExtension.class)
class TarifaServiceTest {

    @Mock
    private TarifaRepository tarifaRepository;

    @InjectMocks
    private TarifaService tarifaService;

    private Tarifa tarifaTest;

    @BeforeEach
    void setUp() {
        tarifaTest = new Tarifa();
        tarifaTest.setId(1L);
        tarifaTest.setDescripcion("Tarifa estándar contenedores 20ft");
        tarifaTest.setCostoBaseKm(45.0);
        tarifaTest.setValorLitroCombustible(150.0);
        tarifaTest.setCargoGestionFijo(1000.0);
        tarifaTest.setPesoMinimo(0.0);
        tarifaTest.setPesoMaximo(15000.0);
        tarifaTest.setVolumenMinimo(0.0);
        tarifaTest.setVolumenMaximo(50.0);
    }

    /**
     * RF10: Test para crear tarifa
     */
    @Test
    void testCrearTarifa() {
        // Given
        when(tarifaRepository.save(any(Tarifa.class))).thenReturn(tarifaTest);

        // When
        Tarifa resultado = tarifaService.save(tarifaTest);

        // Then
        assertNotNull(resultado);
        assertEquals("Tarifa estándar contenedores 20ft", resultado.getDescripcion());
        assertEquals(45.0, resultado.getCostoBaseKm());
        assertEquals(150.0, resultado.getValorLitroCombustible());
        assertEquals(1000.0, resultado.getCargoGestionFijo());
        verify(tarifaRepository).save(any(Tarifa.class));
    }

    /**
     * RF10: Test para buscar tarifa por ID
     */
    @Test
    void testBuscarTarifaPorId() {
        // Given
        when(tarifaRepository.findById(1L)).thenReturn(Optional.of(tarifaTest));

        // When
        Optional<Tarifa> resultado = tarifaService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Tarifa estándar contenedores 20ft", resultado.get().getDescripcion());
        verify(tarifaRepository).findById(1L);
    }

    /**
     * RF10: Test para listar todas las tarifas
     */
    @Test
    void testListarTodasLasTarifas() {
        // Given
        Tarifa tarifa2 = new Tarifa();
        tarifa2.setId(2L);
        tarifa2.setDescripcion("Tarifa contenedores 40ft");
        tarifa2.setCostoBaseKm(60.0);
        tarifa2.setValorLitroCombustible(150.0);
        tarifa2.setCargoGestionFijo(1500.0);
        tarifa2.setPesoMinimo(15000.0);
        tarifa2.setPesoMaximo(30000.0);
        
        List<Tarifa> tarifas = Arrays.asList(tarifaTest, tarifa2);
        when(tarifaRepository.findAll()).thenReturn(tarifas);

        // When
        List<Tarifa> resultado = tarifaService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(tarifaRepository).findAll();
    }

    /**
     * RF10: Test para actualizar tarifa
     */
    @Test
    void testActualizarTarifa() {
        // Given
        tarifaTest.setValorLitroCombustible(160.0);
        tarifaTest.setCostoBaseKm(50.0);
        when(tarifaRepository.save(any(Tarifa.class))).thenReturn(tarifaTest);

        // When
        Tarifa resultado = tarifaService.save(tarifaTest);

        // Then
        assertNotNull(resultado);
        assertEquals(160.0, resultado.getValorLitroCombustible());
        assertEquals(50.0, resultado.getCostoBaseKm());
        verify(tarifaRepository).save(any(Tarifa.class));
    }

    /**
     * RF10: Test para eliminar tarifa
     */
    @Test
    void testEliminarTarifa() {
        // Given
        Long tarifaId = 1L;
        doNothing().when(tarifaRepository).deleteById(tarifaId);

        // When
        tarifaService.delete(tarifaId);

        // Then
        verify(tarifaRepository).deleteById(tarifaId);
    }

    /**
     * Test para validar rangos de peso y volumen en tarifa
     */
    @Test
    void testTarifaConRangosValidos() {
        // Given
        tarifaTest.setPesoMinimo(5000.0);
        tarifaTest.setPesoMaximo(10000.0);
        tarifaTest.setVolumenMinimo(30.0);
        tarifaTest.setVolumenMaximo(50.0);
        
        when(tarifaRepository.save(any(Tarifa.class))).thenReturn(tarifaTest);

        // When
        Tarifa resultado = tarifaService.save(tarifaTest);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getPesoMinimo() < resultado.getPesoMaximo());
        assertTrue(resultado.getVolumenMinimo() < resultado.getVolumenMaximo());
        verify(tarifaRepository).save(any(Tarifa.class));
    }
}
