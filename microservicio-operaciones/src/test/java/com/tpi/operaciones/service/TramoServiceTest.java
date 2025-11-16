package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.repository.TramoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para TramoService
 * RF6: Asignar camión a un tramo de traslado
 * RF7: Determinar inicio o fin de un tramo de traslado
 */
@ExtendWith(MockitoExtension.class)
class TramoServiceTest {

    @Mock
    private TramoRepository tramoRepository;

    @Mock
    private CamionService camionService;

    @Mock
    private CostoService costoService;

    @InjectMocks
    private TramoService tramoService;

    private Tramo tramoTest;

    @BeforeEach
    void setUp() {
        tramoTest = new Tramo();
        tramoTest.setId(1L);
        tramoTest.setOrigen("Buenos Aires");
        tramoTest.setDestino("Rosario");
        tramoTest.setLatitudOrigen(-34.6037);
        tramoTest.setLongitudOrigen(-58.3816);
        tramoTest.setLatitudDestino(-32.9468);
        tramoTest.setLongitudDestino(-60.6393);
        tramoTest.setDistancia(300.0);
        tramoTest.setEstado("estimado");
        tramoTest.setTipoTramo("origen-destino");
    }

    /**
     * RF6: Test para asignar camión a un tramo con capacidad suficiente
     */
    @Test
    void testAsignarCamionConCapacidadSuficiente() {
        // Given
        String dominioCamion = "ABC123";
        Double peso = 8000.0;
        Double volumen = 40.0;
        Double costoEsperado = 31750.0;

        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));
        when(camionService.puedeTransportar(dominioCamion, peso, volumen)).thenReturn(true);
        when(costoService.calcularCostoRealTramo(any(Tramo.class), anyString())).thenReturn(costoEsperado);
        when(tramoRepository.save(any(Tramo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(camionService).marcarOcupado(dominioCamion);

        // When
        Tramo resultado = tramoService.asignarCamion(1L, dominioCamion, peso, volumen);

        // Then
        assertNotNull(resultado);
        assertEquals(dominioCamion, resultado.getCamionDominio());
        assertEquals("asignado", resultado.getEstado());
        assertEquals(costoEsperado, resultado.getCostoAproximado());
        
        verify(tramoRepository).findById(1L);
        verify(camionService).puedeTransportar(dominioCamion, peso, volumen);
        verify(costoService).calcularCostoRealTramo(any(Tramo.class), anyString());
        verify(camionService).marcarOcupado(dominioCamion);
        verify(tramoRepository).save(any(Tramo.class));
    }

    /**
     * RF6 y RF11: Test para asignar camión sin capacidad suficiente
     */
    @Test
    void testAsignarCamionSinCapacidadSuficiente() {
        // Given
        String dominioCamion = "ABC123";
        Double peso = 15000.0; // Excede capacidad
        Double volumen = 100.0;

        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));
        when(camionService.puedeTransportar(dominioCamion, peso, volumen)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tramoService.asignarCamion(1L, dominioCamion, peso, volumen);
        });
        
        assertEquals("El camión no tiene capacidad suficiente para este contenedor", exception.getMessage());
        verify(tramoRepository).findById(1L);
        verify(camionService).puedeTransportar(dominioCamion, peso, volumen);
        verify(camionService, never()).marcarOcupado(anyString());
        verify(tramoRepository, never()).save(any(Tramo.class));
    }

    /**
     * RF6: Test para asignar camión a tramo inexistente
     */
    @Test
    void testAsignarCamionATramoInexistente() {
        // Given
        when(tramoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tramoService.asignarCamion(999L, "ABC123", 5000.0, 30.0);
        });
        
        verify(tramoRepository).findById(999L);
        verify(camionService, never()).puedeTransportar(anyString(), any(), any());
    }

    /**
     * RF7: Test para iniciar un tramo asignado
     */
    @Test
    void testIniciarTramoAsignado() {
        // Given
        tramoTest.setEstado("asignado");
        tramoTest.setCamionDominio("ABC123");
        
        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));
        when(tramoRepository.save(any(Tramo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Tramo resultado = tramoService.iniciarTramo(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("iniciado", resultado.getEstado());
        assertNotNull(resultado.getFechaHoraInicio());
        
        verify(tramoRepository).findById(1L);
        verify(tramoRepository).save(any(Tramo.class));
    }

    /**
     * RF7: Test para iniciar tramo que no está asignado
     */
    @Test
    void testIniciarTramoNoAsignado() {
        // Given
        tramoTest.setEstado("estimado");
        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tramoService.iniciarTramo(1L);
        });
        
        assertEquals("El tramo debe estar asignado para poder iniciarse", exception.getMessage());
        verify(tramoRepository).findById(1L);
        verify(tramoRepository, never()).save(any(Tramo.class));
    }

    /**
     * RF7: Test para finalizar un tramo iniciado
     */
    @Test
    void testFinalizarTramoIniciado() {
        // Given
        tramoTest.setEstado("iniciado");
        tramoTest.setCamionDominio("ABC123");
        tramoTest.setFechaHoraInicio(LocalDateTime.now().minusHours(5));
        Double costoReal = 32000.0;
        
        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));
        when(costoService.calcularCostoRealTramo(any(Tramo.class), anyString())).thenReturn(costoReal);
        when(tramoRepository.save(any(Tramo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(camionService).marcarDisponible(anyString());

        // When
        Tramo resultado = tramoService.finalizarTramo(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("finalizado", resultado.getEstado());
        assertNotNull(resultado.getFechaHoraFin());
        assertEquals(costoReal, resultado.getCostoReal());
        
        verify(tramoRepository).findById(1L);
        verify(costoService).calcularCostoRealTramo(any(Tramo.class), anyString());
        verify(camionService).marcarDisponible("ABC123");
        verify(tramoRepository).save(any(Tramo.class));
    }

    /**
     * RF7: Test para finalizar tramo que no está iniciado
     */
    @Test
    void testFinalizarTramoNoIniciado() {
        // Given
        tramoTest.setEstado("asignado");
        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tramoService.finalizarTramo(1L);
        });
        
        assertEquals("El tramo debe estar iniciado para poder finalizarse", exception.getMessage());
        verify(tramoRepository).findById(1L);
        verify(tramoRepository, never()).save(any(Tramo.class));
    }

    /**
     * Test para crear tramo con cálculo automático de distancia
     */
    @Test
    void testCrearTramoConCalculoAutomaticoDistancia() {
        // Given
        Tramo nuevoTramo = new Tramo();
        nuevoTramo.setOrigen("CABA");
        nuevoTramo.setDestino("La Plata");
        nuevoTramo.setLatitudOrigen(-34.6037);
        nuevoTramo.setLongitudOrigen(-58.3816);
        nuevoTramo.setLatitudDestino(-34.9214);
        nuevoTramo.setLongitudDestino(-57.9544);
        
        when(tramoRepository.save(any(Tramo.class))).thenAnswer(invocation -> {
            Tramo t = invocation.getArgument(0);
            t.setId(2L);
            return t;
        });

        // When
        Tramo resultado = tramoService.crearTramo(nuevoTramo);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getDistancia());
        assertTrue(resultado.getDistancia() > 0);
        assertEquals("estimado", resultado.getEstado());
        verify(tramoRepository).save(any(Tramo.class));
    }

    /**
     * Test para listar todos los tramos
     */
    @Test
    void testListarTodosLosTramos() {
        // Given
        Tramo tramo2 = new Tramo();
        tramo2.setId(2L);
        tramo2.setOrigen("Córdoba");
        tramo2.setDestino("Mendoza");
        
        List<Tramo> tramos = Arrays.asList(tramoTest, tramo2);
        when(tramoRepository.findAll()).thenReturn(tramos);

        // When
        List<Tramo> resultado = tramoService.listarTramos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(tramoRepository).findAll();
    }

    /**
     * Test para buscar tramo por ID
     */
    @Test
    void testBuscarTramoPorId() {
        // Given
        when(tramoRepository.findById(1L)).thenReturn(Optional.of(tramoTest));

        // When
        Optional<Tramo> resultado = tramoService.findById(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Buenos Aires", resultado.get().getOrigen());
        assertEquals("Rosario", resultado.get().getDestino());
        verify(tramoRepository).findById(1L);
    }
}
