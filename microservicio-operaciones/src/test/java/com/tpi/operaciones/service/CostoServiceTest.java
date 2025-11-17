package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Camion;
import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.model.Tarifa;
import com.tpi.operaciones.model.Tramo;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para CostoService
 * RF8: Calcular el costo total de la entrega
 */
@ExtendWith(MockitoExtension.class)
class CostoServiceTest {

    @Mock
    private TarifaService tarifaService;

    @Mock
    private CamionService camionService;

    @Mock
    private DepositoService depositoService;

    @InjectMocks
    private CostoService costoService;

    private Camion camionTest;
    private Tarifa tarifaTest;
    private Tramo tramoTest;
    private Deposito depositoTest;

    @BeforeEach
    void setUp() {
        // Setup camión de prueba
        camionTest = new Camion();
        camionTest.setDominio("ABC123");
        camionTest.setNombreTransportista("Carlos Pérez");
        camionTest.setCapacidadPeso(10000.0);
        camionTest.setCapacidadVolumen(50.0);
        camionTest.setConsumoPorKm(0.35);
        camionTest.setCostoBasePorKm(50.0);
        camionTest.setDisponible(true);

        // Setup tarifa de prueba
        tarifaTest = new Tarifa();
        tarifaTest.setId(1L);
        tarifaTest.setDescripcion("Tarifa estándar");
        tarifaTest.setCostoBaseKm(45.0);
        tarifaTest.setValorLitroCombustible(150.0);
        tarifaTest.setCargoGestionFijo(1000.0);
        tarifaTest.setPesoMinimo(0.0);
        tarifaTest.setPesoMaximo(15000.0);
        tarifaTest.setVolumenMinimo(0.0);
        tarifaTest.setVolumenMaximo(100.0);

        // Setup tramo de prueba
        tramoTest = new Tramo();
        tramoTest.setId(1L);
        tramoTest.setOrigen("Buenos Aires");
        tramoTest.setDestino("Rosario");
        tramoTest.setDistancia(300.0);
        tramoTest.setEstado("estimado");
        tramoTest.setTipoTramo("origen-destino");

        // Setup depósito de prueba
        depositoTest = new Deposito();
        depositoTest.setId(1L);
        depositoTest.setNombre("Depósito Central");
        depositoTest.setCostoEstadiaDiaria(500.0);
        depositoTest.setLatitud(-34.6037);
        depositoTest.setLongitud(-58.3816);
    }

    /**
     * RF8: Test para calcular costo estimado de un tramo
     */
    @Test
    void testCalcularCostoEstimadoTramo() {
        // Given
        Double distancia = 300.0;
        Double peso = 8000.0;
        Double volumen = 40.0;

        List<Camion> camionesAptos = Arrays.asList(camionTest);
        List<Tarifa> tarifas = Arrays.asList(tarifaTest);

        when(camionService.findAptosParaContenedor(peso, volumen)).thenReturn(camionesAptos);
        when(tarifaService.findAll()).thenReturn(tarifas);

        // When
        Double resultado = costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado > 0);
        
        // Verificar cálculo aproximado:
        // cargo gestión + (costo base * distancia) + (consumo * distancia * valor combustible)
        // 1000 + (50 * 300) + (0.35 * 300 * 150) = 1000 + 15000 + 15750 = 31750
        assertEquals(31750.0, resultado, 0.01);
        
        verify(camionService).findAptosParaContenedor(peso, volumen);
        verify(tarifaService).findAll();
    }

    /**
     * RF8: Test para calcular costo estimado sin camiones disponibles
     */
    @Test
    void testCalcularCostoEstimadoSinCamionesDisponibles() {
        // Given
        Double distancia = 300.0;
        Double peso = 8000.0;
        Double volumen = 40.0;

        when(camionService.findAptosParaContenedor(peso, volumen)).thenReturn(Arrays.asList());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);
        });
    }

    /**
     * RF8: Test para calcular costo real de un tramo con camión específico
     */
    @Test
    void testCalcularCostoRealTramo() {
        // Given
        tramoTest.setDistancia(300.0);
        List<Tarifa> tarifas = Arrays.asList(tarifaTest);

        when(camionService.findById("ABC123")).thenReturn(Optional.of(camionTest));
        when(tarifaService.findAll()).thenReturn(tarifas);

        // When
        Double resultado = costoService.calcularCostoRealTramo(tramoTest, "ABC123");

        // Then
        assertNotNull(resultado);
        assertTrue(resultado > 0);
        
        // Verificar cálculo:
        // cargo gestión + (costo base camión * distancia) + (consumo camión * distancia * valor combustible)
        // 1000 + (50 * 300) + (0.35 * 300 * 150) = 1000 + 15000 + 15750 = 31750
        assertEquals(31750.0, resultado, 0.01);
        
        verify(camionService).findById("ABC123");
        verify(tarifaService).findAll();
    }

    /**
     * RF8: Test para calcular costo real con camión inexistente
     */
    @Test
    void testCalcularCostoRealCamionInexistente() {
        // Given
        tramoTest.setDistancia(300.0);
        when(camionService.findById("NOEXISTE")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            costoService.calcularCostoRealTramo(tramoTest, "NOEXISTE");
        });
    }

    /**
     * RF8: Test para calcular costo real sin distancia definida
     */
    @Test
    void testCalcularCostoRealSinDistancia() {
        // Given
        tramoTest.setDistancia(null);
        when(camionService.findById("ABC123")).thenReturn(Optional.of(camionTest));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            costoService.calcularCostoRealTramo(tramoTest, "ABC123");
        });
    }

    /**
     * RF8: Test para calcular costo de estadía en depósito
     */
    @Test
    void testCalcularCostoEstadia() {
        // Given
        LocalDateTime entrada = LocalDateTime.of(2025, 11, 1, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 11, 3, 14, 0);
        
        when(depositoService.findById(1L)).thenReturn(Optional.of(depositoTest));

        // When
        Double resultado = costoService.calcularCostoEstadia(1L, entrada, salida);

        // Then
        assertNotNull(resultado);
        // 2 días y 4 horas = 3 días redondeando hacia arriba
        // 3 días * 500 = 1500
        assertEquals(1500.0, resultado, 0.01);
        
        verify(depositoService).findById(1L);
    }

    /**
     * RF8: Test para calcular costo de estadía sin fechas
     */
    @Test
    void testCalcularCostoEstadiaSinFechas() {
        // Given
        when(depositoService.findById(1L)).thenReturn(Optional.of(depositoTest));

        // When
        Double resultado = costoService.calcularCostoEstadia(1L, null, null);

        // Then
        assertEquals(0.0, resultado);
        verify(depositoService).findById(1L);
    }

    /**
     * RF8: Test para calcular costo de estadía con depósito inexistente
     */
    @Test
    void testCalcularCostoEstadiaDepositoInexistente() {
        // Given
        LocalDateTime entrada = LocalDateTime.of(2025, 11, 1, 10, 0);
        LocalDateTime salida = LocalDateTime.of(2025, 11, 3, 14, 0);
        
        when(depositoService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            costoService.calcularCostoEstadia(999L, entrada, salida);
        });
    }

    /**
     * Test para calcular tiempo estimado de viaje
     */
    @Test
    void testCalcularTiempoEstimado() {
        // Given
        Double distancia = 300.0;

        // When
        Integer resultado = costoService.calcularTiempoEstimado(distancia);

        // Then
        assertNotNull(resultado);
        // 300 km / 60 km/h = 5 horas
        assertEquals(5, resultado);
    }

    /**
     * Test para calcular tiempo estimado con distancia decimal
     */
    @Test
    void testCalcularTiempoEstimadoConDecimal() {
        // Given
        Double distancia = 350.0;

        // When
        Integer resultado = costoService.calcularTiempoEstimado(distancia);

        // Then
        assertNotNull(resultado);
        // 350 km / 60 km/h = 5.83 horas, redondeando hacia arriba = 6
        assertEquals(6, resultado);
    }

    /**
     * Test para calcular costo estimado con tarifas vacías (valores por defecto)
     */
    @Test
    void testCalcularCostoEstimadoConTarifasPorDefecto() {
        // Given
        Double distancia = 300.0;
        Double peso = 8000.0;
        Double volumen = 40.0;

        List<Camion> camionesAptos = Arrays.asList(camionTest);

        when(camionService.findAptosParaContenedor(peso, volumen)).thenReturn(camionesAptos);
        when(tarifaService.findAll()).thenReturn(Arrays.asList());

        // When
        Double resultado = costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado > 0);
        // Con valores por defecto: cargo=1000, combustible=150
        verify(camionService).findAptosParaContenedor(peso, volumen);
    }
}
