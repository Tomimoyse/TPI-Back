package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Cliente;
import com.tpi.solicitudes.repository.ClienteRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para ClienteService
 * Valida operaciones CRUD de clientes y búsqueda por email
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente();
        clienteTest.setId(1L);
        clienteTest.setNombre("María González");
        clienteTest.setEmail("maria@example.com");
        clienteTest.setTelefono("+54911234567");
        clienteTest.setDireccion("Av. Santa Fe 2500");
        clienteTest.setDocumento("23456789");
    }

    @Test
    void testCrearCliente() {
        // Given
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteTest);

        // When
        Cliente resultado = clienteService.save(clienteTest);

        // Then
        assertNotNull(resultado);
        assertEquals("María González", resultado.getNombre());
        assertEquals("maria@example.com", resultado.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void testBuscarClientePorId() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteTest));

        // When
        Optional<Cliente> resultado = clienteService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("María González", resultado.get().getNombre());
        verify(clienteRepository).findById(1L);
    }

    @Test
    void testBuscarClientePorEmail() {
        // Given
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.of(clienteTest));

        // When
        Optional<Cliente> resultado = clienteService.findByEmail("maria@example.com");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("maria@example.com", resultado.get().getEmail());
        verify(clienteRepository).findByEmail("maria@example.com");
    }

    @Test
    void testBuscarClientePorEmailInexistente() {
        // Given
        when(clienteRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = clienteService.findByEmail("noexiste@example.com");

        // Then
        assertFalse(resultado.isPresent());
        verify(clienteRepository).findByEmail("noexiste@example.com");
    }

    @Test
    void testListarTodosLosClientes() {
        // Given
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombre("Pedro Ramírez");
        cliente2.setEmail("pedro@example.com");
        
        List<Cliente> clientes = Arrays.asList(clienteTest, cliente2);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // When
        List<Cliente> resultado = clienteService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(clienteRepository).findAll();
    }

    @Test
    void testEliminarCliente() {
        // Given
        Long clienteId = 1L;
        doNothing().when(clienteRepository).deleteById(clienteId);

        // When
        clienteService.delete(clienteId);

        // Then
        verify(clienteRepository).deleteById(clienteId);
    }
}
