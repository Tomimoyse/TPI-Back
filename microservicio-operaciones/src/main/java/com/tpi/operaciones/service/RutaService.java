package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Deposito;
import com.tpi.operaciones.model.Ruta;
import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.repository.RutaRepository;
import com.tpi.operaciones.repository.TramoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RutaService {
    @Autowired
    private RutaRepository rutaRepository;
    
    @Autowired
    private TramoRepository tramoRepository;
    
    @Autowired
    private TramoService tramoService;
    
    @Autowired
    private DepositoService depositoService;
    
    @Autowired
    private CostoService costoService;

    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> findById(Long id) {
        return rutaRepository.findById(id);
    }

    public Ruta save(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    public void delete(Long id) {
        rutaRepository.deleteById(id);
    }

    @Transactional
    public Ruta asignarTramos(Long rutaId, List<Long> tramoIds) {
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        List<Tramo> tramos = tramoRepository.findAllById(tramoIds);
        ruta.setTramos(tramos);
        return rutaRepository.save(ruta);
    }

    public Boolean tieneTramos(Long rutaId) {
        return rutaRepository.findById(rutaId)
                .map(r -> r.getTramos() != null && !r.getTramos().isEmpty())
                .orElse(false);
    }
    
    /**
     * Requerimiento 3: Calcular ruta tentativa con tramos sugeridos
     * Crea una ruta directa (origen -> destino) o con depósitos intermedios
     */
    @Transactional
    public Ruta calcularRutaTentativa(
            Long solicitudId,
            Double latOrigen, Double lonOrigen, String dirOrigen,
            Double latDestino, Double lonDestino, String dirDestino,
            List<Long> depositoIds,
            Double peso, Double volumen) {
        
        Ruta ruta = new Ruta();
        ruta.setSolicitudId(solicitudId);
        ruta.setNombre("Ruta para Solicitud " + solicitudId);
        
        List<Tramo> tramos = new ArrayList<>();
        Double distanciaTotal = 0.0;
        Double costoTotal = 0.0;
        Integer tiempoTotal = 0;
        
        if (depositoIds == null || depositoIds.isEmpty()) {
            // Ruta directa: origen -> destino
            Tramo tramo = new Tramo();
            tramo.setOrigen(dirOrigen);
            tramo.setDestino(dirDestino);
            tramo.setLatitudOrigen(latOrigen);
            tramo.setLongitudOrigen(lonOrigen);
            tramo.setLatitudDestino(latDestino);
            tramo.setLongitudDestino(lonDestino);
            tramo.setTipoTramo("origen-destino");
            tramo.setEstado("estimado");
            
            Double distancia = tramoService.calcularDistanciaPorCoordenadas(
                latOrigen, lonOrigen, latDestino, lonDestino);
            tramo.setDistancia(distancia);
            
            Double costo = costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);
            tramo.setCostoAproximado(costo);
            
            tramos.add(tramoService.crearTramo(tramo));
            distanciaTotal += distancia;
            costoTotal += costo;
            tiempoTotal += costoService.calcularTiempoEstimado(distancia);
            
        } else {
            // Ruta con depósitos
            Double latActual = latOrigen;
            Double lonActual = lonOrigen;
            String dirActual = dirOrigen;
            String tipoTramoAnterior = "origen";
            
            for (int i = 0; i < depositoIds.size(); i++) {
                final Long depositoId = depositoIds.get(i);
                Deposito deposito = depositoService.findById(depositoId)
                    .orElseThrow(() -> new RuntimeException("Depósito no encontrado: " + depositoId));
                
                Tramo tramo = new Tramo();
                tramo.setOrigen(dirActual);
                tramo.setDestino(deposito.getDireccion());
                tramo.setLatitudOrigen(latActual);
                tramo.setLongitudOrigen(lonActual);
                tramo.setLatitudDestino(deposito.getLatitud());
                tramo.setLongitudDestino(deposito.getLongitud());
                tramo.setTipoTramo(tipoTramoAnterior + "-deposito");
                tramo.setEstado("estimado");
                tramo.setDepositoId(deposito.getId());
                
                Double distancia = tramoService.calcularDistanciaPorCoordenadas(
                    latActual, lonActual, deposito.getLatitud(), deposito.getLongitud());
                tramo.setDistancia(distancia);
                
                Double costo = costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);
                tramo.setCostoAproximado(costo);
                
                tramos.add(tramoService.crearTramo(tramo));
                distanciaTotal += distancia;
                costoTotal += costo;
                tiempoTotal += costoService.calcularTiempoEstimado(distancia);
                
                latActual = deposito.getLatitud();
                lonActual = deposito.getLongitud();
                dirActual = deposito.getDireccion();
                tipoTramoAnterior = "deposito";
            }
            
            // Último tramo: último depósito -> destino
            Tramo tramoFinal = new Tramo();
            tramoFinal.setOrigen(dirActual);
            tramoFinal.setDestino(dirDestino);
            tramoFinal.setLatitudOrigen(latActual);
            tramoFinal.setLongitudOrigen(lonActual);
            tramoFinal.setLatitudDestino(latDestino);
            tramoFinal.setLongitudDestino(lonDestino);
            tramoFinal.setTipoTramo("deposito-destino");
            tramoFinal.setEstado("estimado");
            
            Double distancia = tramoService.calcularDistanciaPorCoordenadas(
                latActual, lonActual, latDestino, lonDestino);
            tramoFinal.setDistancia(distancia);
            
            Double costo = costoService.calcularCostoEstimadoTramo(distancia, peso, volumen);
            tramoFinal.setCostoAproximado(costo);
            
            tramos.add(tramoService.crearTramo(tramoFinal));
            distanciaTotal += distancia;
            costoTotal += costo;
            tiempoTotal += costoService.calcularTiempoEstimado(distancia);
        }
        
        ruta.setTramos(tramos);
        ruta.setCantidadTramos(tramos.size());
        ruta.setCantidadDepositos(depositoIds != null ? depositoIds.size() : 0);
        ruta.setDistanciaTotal(distanciaTotal);
        ruta.setCostoEstimado(costoTotal);
        ruta.setTiempoEstimadoHoras(tiempoTotal);
        
        return save(ruta);
    }
    
    /**
     * Calcula el costo y tiempo real de una ruta basado en los tramos finalizados
     */
    @Transactional
    public Ruta calcularCostoTiempoReal(Long rutaId) {
        Ruta ruta = findById(rutaId)
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        
        Double costoReal = 0.0;
        Integer tiempoRealHoras = 0;
        
        for (Tramo tramo : ruta.getTramos()) {
            if (tramo.getCostoReal() != null) {
                costoReal += tramo.getCostoReal();
            }
            
            if (tramo.getFechaHoraInicio() != null && tramo.getFechaHoraFin() != null) {
                long horas = java.time.Duration.between(
                    tramo.getFechaHoraInicio(), 
                    tramo.getFechaHoraFin()
                ).toHours();
                tiempoRealHoras += (int) horas;
            }
        }
        
        ruta.setCostoReal(costoReal);
        ruta.setTiempoRealHoras(tiempoRealHoras);
        
        return save(ruta);
    }
}
