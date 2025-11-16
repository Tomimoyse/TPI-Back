package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.repository.TramoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TramoService {

    @Autowired
    private TramoRepository tramoRepository;
    
    @Autowired
    private CamionService camionService;
    
    @Autowired
    private CostoService costoService;

    @Value("${google.maps.api-key:}")
    private String apiKey;

    public List<Tramo> listarTramos() {
        return tramoRepository.findAll();
    }
    
    public Optional<Tramo> findById(Long id) {
        return tramoRepository.findById(id);
    }
    
    public Tramo save(Tramo tramo) {
        return tramoRepository.save(tramo);
    }

    public Tramo crearTramo(Tramo tramo) {
        // Calcular distancia automáticamente al crear si hay coordenadas
        if ((tramo.getDistancia() == null || tramo.getDistancia() == 0) && 
            tramo.getLatitudOrigen() != null && tramo.getLongitudOrigen() != null &&
            tramo.getLatitudDestino() != null && tramo.getLongitudDestino() != null) {
            
            Double distancia = calcularDistanciaPorCoordenadas(
                tramo.getLatitudOrigen(), tramo.getLongitudOrigen(),
                tramo.getLatitudDestino(), tramo.getLongitudDestino()
            );
            tramo.setDistancia(distancia);
        }
        
        // Estado inicial
        if (tramo.getEstado() == null) {
            tramo.setEstado("estimado");
        }
        
        return tramoRepository.save(tramo);
    }
    
    /**
     * Requerimiento 6: Asignar camión a un tramo de traslado
     */
    public Tramo asignarCamion(Long tramoId, String camionDominio, Double peso, Double volumen) {
        Tramo tramo = findById(tramoId)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        
        // Requerimiento 11: Validar que el camión no supere su capacidad
        if (!camionService.puedeTransportar(camionDominio, peso, volumen)) {
            throw new RuntimeException("El camión no tiene capacidad suficiente para este contenedor");
        }
        
        tramo.setCamionDominio(camionDominio);
        tramo.setEstado("asignado");
        
        // Calcular costo aproximado si hay distancia
        if (tramo.getDistancia() != null && tramo.getDistancia() > 0) {
            Double costo = costoService.calcularCostoRealTramo(tramo, camionDominio);
            tramo.setCostoAproximado(costo);
        }
        
        // Marcar camión como ocupado
        camionService.marcarOcupado(camionDominio);
        
        return save(tramo);
    }
    
    /**
     * Requerimiento 7: Iniciar un tramo de traslado (Transportista)
     */
    public Tramo iniciarTramo(Long tramoId) {
        Tramo tramo = findById(tramoId)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        
        if (!"asignado".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo debe estar asignado para poder iniciarse");
        }
        
        tramo.setFechaHoraInicio(LocalDateTime.now());
        tramo.setEstado("iniciado");
        
        return save(tramo);
    }
    
    /**
     * Requerimiento 7: Finalizar un tramo de traslado (Transportista)
     */
    public Tramo finalizarTramo(Long tramoId) {
        Tramo tramo = findById(tramoId)
            .orElseThrow(() -> new RuntimeException("Tramo no encontrado"));
        
        if (!"iniciado".equals(tramo.getEstado())) {
            throw new RuntimeException("El tramo debe estar iniciado para poder finalizarse");
        }
        
        tramo.setFechaHoraFin(LocalDateTime.now());
        tramo.setEstado("finalizado");
        
        // Calcular costo real
        if (tramo.getCamionDominio() != null) {
            Double costoReal = costoService.calcularCostoRealTramo(tramo, tramo.getCamionDominio());
            tramo.setCostoReal(costoReal);
        }
        
        // Liberar camión
        if (tramo.getCamionDominio() != null) {
            camionService.marcarDisponible(tramo.getCamionDominio());
        }
        
        return save(tramo);
    }

    public Double calcularDistancia(String origen, String destino) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                // Si no hay API key, devolver una distancia estimada
                return 100.0;
            }
            
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                origen.replace(" ", "+"),
                destino.replace(" ", "+"),
                apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            
            // Parsear respuesta JSON simple
            int valueIndex = response.indexOf("\"value\" :");
            if (valueIndex != -1) {
                String valueStr = response.substring(valueIndex + 10);
                int endIndex = valueStr.indexOf(",");
                if (endIndex == -1) endIndex = valueStr.indexOf("}");
                
                String distanceStr = valueStr.substring(0, endIndex).trim();
                double distanceMeters = Double.parseDouble(distanceStr);
                return distanceMeters / 1000; // Convertir a kilómetros
            }
            
            return 0.0;
        } catch (Exception e) {
            // En caso de error, devolver distancia estimada
            return 100.0;
        }
    }
    
    /**
     * Calcula distancia usando coordenadas geográficas (latitud/longitud)
     */
    public Double calcularDistanciaPorCoordenadas(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                // Usar fórmula de Haversine como fallback
                return calcularDistanciaHaversine(lat1, lon1, lat2, lon2);
            }
            
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f&destinations=%f,%f&key=%s",
                lat1, lon1, lat2, lon2, apiKey
            );

            String response = restTemplate.getForObject(url, String.class);
            
            int valueIndex = response.indexOf("\"value\" :");
            if (valueIndex != -1) {
                String valueStr = response.substring(valueIndex + 10);
                int endIndex = valueStr.indexOf(",");
                if (endIndex == -1) endIndex = valueStr.indexOf("}");
                
                String distanceStr = valueStr.substring(0, endIndex).trim();
                double distanceMeters = Double.parseDouble(distanceStr);
                return distanceMeters / 1000;
            }
            
            return calcularDistanciaHaversine(lat1, lon1, lat2, lon2);
        } catch (Exception e) {
            return calcularDistanciaHaversine(lat1, lon1, lat2, lon2);
        }
    }
    
    /**
     * Fórmula de Haversine para calcular distancia entre dos puntos geográficos
     */
    private Double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
