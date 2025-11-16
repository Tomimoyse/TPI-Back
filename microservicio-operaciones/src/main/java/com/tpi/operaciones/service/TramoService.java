package com.tpi.operaciones.service;

import com.tpi.operaciones.model.Tramo;
import com.tpi.operaciones.repository.TramoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TramoService {

    @Autowired
    private TramoRepository tramoRepository;

    @Value("${google.maps.api-key}")
    private String apiKey;

    public List<Tramo> listarTramos() {
        return tramoRepository.findAll();
    }

    public Tramo crearTramo(Tramo tramo) {
        // Calcular distancia automáticamente al crear
        if (tramo.getDistancia() == null || tramo.getDistancia() == 0) {
            Double distancia = calcularDistancia(tramo.getOrigen(), tramo.getDestino());
            tramo.setDistancia(distancia);
        }
        return tramoRepository.save(tramo);
    }

    public Double calcularDistancia(String origen, String destino) {
        try {
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
            throw new RuntimeException("Error al calcular distancia con Google Maps API: " + e.getMessage());
        }
    }
}
