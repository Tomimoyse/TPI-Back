package com.tpi.solicitudes.service;

import com.tpi.solicitudes.model.Cliente;
import com.tpi.solicitudes.model.Contenedor;
import com.tpi.solicitudes.model.Solicitud;
import com.tpi.solicitudes.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para SolicitudService - Validación de Requerimientos Funcionales
 * RF1: Registrar nueva solicitud de transporte
 * RF2: Consultar estado del transporte
 * RF4: Asignar ruta a la solicitud
 * RF5: Consultar contenedores pendientes
 * RF9: Registrar costo y tiempo real al finalizar
 */
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ContenedorService contenedorService;

    @InjectMocks
    private SolicitudService solicitudService;

    private Cliente clienteTest;
    private Contenedor contenedorTest;
    private Solicitud solicitudTest;

    @BeforeEach
    void setUp() {
        // Setup cliente de prueba
        clienteTest = new Cliente();
        clienteTest.setId(1L);
        clienteTest.setNombre("Juan Pérez");
        clienteTest.setEmail("juan@example.com");
        clienteTest.setTelefono("+54911234567");
        clienteTest.setDireccion("Av. Corrientes 1234");
        clienteTest.setDocumento("12345678");

        // Setup contenedor de prueba
        contenedorTest = new Contenedor();
        contenedorTest.setId(1L);
        contenedorTest.setIdentificacion("CONT-001");
        contenedorTest.setTipo("20ft Standard");
        contenedorTest.setPeso(5000.0);
        contenedorTest.setVolumen(33.0);
        contenedorTest.setEstado("disponible");

        // Setup solicitud de prueba
        solicitudTest = new Solicitud();
        solicitudTest.setId(1L);
        solicitudTest.setDescripcion("Transporte de contenedor");
        solicitudTest.setDireccionOrigen("Av. Libertador 5000, CABA");
        solicitudTest.setDireccionDestino("Ruta 9 Km 100");
        solicitudTest.setLatitudOrigen(-34.5731);
        solicitudTest.setLongitudOrigen(-58.4486);
        solicitudTest.setLatitudDestino(-34.9211);
        solicitudTest.setLongitudDestino(-57.9544);
        solicitudTest.setEstado("borrador");
        solicitudTest.setFecha(LocalDate.now());
        solicitudTest.setCliente(clienteTest);
        solicitudTest.setContenedor(contenedorTest);
    }

    /**
     * RF1: Test para crear solicitud completa con cliente existente
     */
    @Test
    void testCrearSolicitudCompletaConClienteExistente() {
        // Given
        when(clienteService.findByEmail(anyString())).thenReturn(Optional.of(clienteTest));
        when(contenedorService.save(any(Contenedor.class))).thenReturn(contenedorTest);
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitudTest);

        // When
        Solicitud resultado = solicitudService.crearSolicitudCompleta(
            solicitudTest, clienteTest, contenedorTest
        );

        // Then
        assertNotNull(resultado);
        assertEquals("borrador", resultado.getEstado());
        assertEquals(clienteTest.getEmail(), resultado.getCliente().getEmail());
        assertNotNull(resultado.getContenedor());
        verify(clienteService).findByEmail(clienteTest.getEmail());
        verify(contenedorService).save(any(Contenedor.class));
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    /**
     * RF1: Test para crear solicitud completa con cliente nuevo
     */
    @Test
    void testCrearSolicitudCompletaConClienteNuevo() {
        // Given
        when(clienteService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(clienteService.save(any(Cliente.class))).thenReturn(clienteTest);
        when(contenedorService.save(any(Contenedor.class))).thenReturn(contenedorTest);
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitudTest);

        // When
        Solicitud resultado = solicitudService.crearSolicitudCompleta(
            solicitudTest, clienteTest, contenedorTest
        );

        // Then
        assertNotNull(resultado);
        assertEquals("borrador", resultado.getEstado());
        verify(clienteService).save(any(Cliente.class));
        verify(contenedorService).save(any(Contenedor.class));
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    /**
     * RF2: Test para consultar estado de una solicitud
     */
    @Test
    void testConsultarEstadoSolicitud() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudTest));

        // When
        Optional<Solicitud> resultado = solicitudService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("borrador", resultado.get().getEstado());
        assertEquals("Transporte de contenedor", resultado.get().getDescripcion());
        verify(solicitudRepository).findById(1L);
    }

    /**
     * RF4: Test para asignar ruta a una solicitud
     */
    @Test
    void testAsignarRutaASolicitud() {
        // Given
        Long rutaId = 100L;
        Double costoEstimado = 50000.0;
        Integer tiempoEstimado = 8;
        
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudTest));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Solicitud resultado = solicitudService.asignarRuta(1L, rutaId, costoEstimado, tiempoEstimado);

        // Then
        assertNotNull(resultado);
        assertEquals(rutaId, resultado.getRutaId());
        assertEquals(costoEstimado, resultado.getCostoEstimado());
        assertEquals(tiempoEstimado, resultado.getTiempoEstimadoHoras());
        assertEquals("programada", resultado.getEstado());
        verify(solicitudRepository).findById(1L);
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    /**
     * RF4: Test para asignar ruta a solicitud inexistente
     */
    @Test
    void testAsignarRutaASolicitudInexistente() {
        // Given
        when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            solicitudService.asignarRuta(999L, 100L, 50000.0, 8);
        });
    }

    /**
     * RF5: Test para consultar solicitudes por estado
     */
    @Test
    void testConsultarSolicitudesPorEstado() {
        // Given
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);
        solicitud2.setEstado("programada");
        
        List<Solicitud> solicitudesProgramadas = Arrays.asList(solicitud2);
        when(solicitudRepository.findByEstado("programada")).thenReturn(solicitudesProgramadas);

        // When
        List<Solicitud> resultado = solicitudService.findByEstado("programada");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("programada", resultado.get(0).getEstado());
        verify(solicitudRepository).findByEstado("programada");
    }

    /**
     * RF9: Test para finalizar solicitud registrando costo y tiempo real
     */
    @Test
    void testFinalizarSolicitudConCostoYTiempoReal() {
        // Given
        Double costoFinal = 52000.0;
        Integer tiempoReal = 9;
        
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudTest));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Solicitud resultado = solicitudService.finalizarSolicitud(1L, costoFinal, tiempoReal);

        // Then
        assertNotNull(resultado);
        assertEquals(costoFinal, resultado.getCostoFinal());
        assertEquals(tiempoReal, resultado.getTiempoRealHoras());
        assertEquals("entregada", resultado.getEstado());
        verify(solicitudRepository).findById(1L);
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    /**
     * Test para actualizar estado de una solicitud
     */
    @Test
    void testActualizarEstadoSolicitud() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudTest));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Solicitud resultado = solicitudService.actualizarEstado(1L, "en tránsito");

        // Then
        assertNotNull(resultado);
        assertEquals("en tránsito", resultado.getEstado());
        verify(solicitudRepository).findById(1L);
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    /**
     * Test para listar todas las solicitudes
     */
    @Test
    void testListarTodasLasSolicitudes() {
        // Given
        List<Solicitud> solicitudes = Arrays.asList(solicitudTest);
        when(solicitudRepository.findAll()).thenReturn(solicitudes);

        // When
        List<Solicitud> resultado = solicitudService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(solicitudRepository).findAll();
    }

    /**
     * Test para consultar solicitudes por cliente
     */
    @Test
    void testConsultarSolicitudesPorCliente() {
        // Given
        List<Solicitud> solicitudes = Arrays.asList(solicitudTest);
        when(solicitudRepository.findByClienteId(1L)).thenReturn(solicitudes);

        // When
        List<Solicitud> resultado = solicitudService.findByClienteId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(clienteTest.getId(), resultado.get(0).getCliente().getId());
        verify(solicitudRepository).findByClienteId(1L);
    }
}
