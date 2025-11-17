package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.repository.DepositoRepository;
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
 * Tests para DepositoService
 * RF10: Registrar y actualizar depósitos
 */
@ExtendWith(MockitoExtension.class)
class DepositoServiceTest {

    @Mock
    private DepositoRepository depositoRepository;

    @InjectMocks
    private DepositoService depositoService;

    private Deposito depositoTest;

    @BeforeEach
    void setUp() {
        depositoTest = new Deposito();
        depositoTest.setId(1L);
        depositoTest.setNombre("Depósito Central CABA");
        depositoTest.setDireccion("Av. Warnes 2500, CABA");
        depositoTest.setLatitud(-34.5989);
        depositoTest.setLongitud(-58.4357);
        depositoTest.setCostoEstadiaDiaria(500.0);
    }

    /**
     * RF10: Test para crear depósito
     */
    @Test
    void testCrearDeposito() {
        // Given
        when(depositoRepository.save(any(Deposito.class))).thenReturn(depositoTest);

        // When
        Deposito resultado = depositoService.save(depositoTest);

        // Then
        assertNotNull(resultado);
        assertEquals("Depósito Central CABA", resultado.getNombre());
        assertEquals(-34.5989, resultado.getLatitud());
        assertEquals(-58.4357, resultado.getLongitud());
        assertEquals(500.0, resultado.getCostoEstadiaDiaria());
        verify(depositoRepository).save(any(Deposito.class));
    }

    /**
     * RF10: Test para buscar depósito por ID
     */
    @Test
    void testBuscarDepositoPorId() {
        // Given
        when(depositoRepository.findById(1L)).thenReturn(Optional.of(depositoTest));

        // When
        Optional<Deposito> resultado = depositoService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Depósito Central CABA", resultado.get().getNombre());
        verify(depositoRepository).findById(1L);
    }

    /**
     * RF10: Test para listar todos los depósitos
     */
    @Test
    void testListarTodosLosDepositos() {
        // Given
        Deposito deposito2 = new Deposito();
        deposito2.setId(2L);
        deposito2.setNombre("Depósito Rosario");
        deposito2.setLatitud(-32.9468);
        deposito2.setLongitud(-60.6393);
        deposito2.setCostoEstadiaDiaria(400.0);
        
        List<Deposito> depositos = Arrays.asList(depositoTest, deposito2);
        when(depositoRepository.findAll()).thenReturn(depositos);

        // When
        List<Deposito> resultado = depositoService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(depositoRepository).findAll();
    }

    /**
     * RF10: Test para actualizar depósito
     */
    @Test
    void testActualizarDeposito() {
        // Given
        depositoTest.setCostoEstadiaDiaria(600.0);
        depositoTest.setDireccion("Av. Warnes 2600, CABA");
        when(depositoRepository.save(any(Deposito.class))).thenReturn(depositoTest);

        // When
        Deposito resultado = depositoService.save(depositoTest);

        // Then
        assertNotNull(resultado);
        assertEquals(600.0, resultado.getCostoEstadiaDiaria());
        assertEquals("Av. Warnes 2600, CABA", resultado.getDireccion());
        verify(depositoRepository).save(any(Deposito.class));
    }

    /**
     * RF10: Test para eliminar depósito
     */
    @Test
    void testEliminarDeposito() {
        // Given
        Long depositoId = 1L;
        doNothing().when(depositoRepository).deleteById(depositoId);

        // When
        depositoService.delete(depositoId);

        // Then
        verify(depositoRepository).deleteById(depositoId);
    }

    /**
     * Test para buscar depósito inexistente
     */
    @Test
    void testBuscarDepositoInexistente() {
        // Given
        when(depositoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Deposito> resultado = depositoService.findById(999L);

        // Then
        assertFalse(resultado.isPresent());
        verify(depositoRepository).findById(999L);
    }
}
