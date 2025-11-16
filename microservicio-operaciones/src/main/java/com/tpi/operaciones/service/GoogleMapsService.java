package com.tpi.operaciones.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.json.JSONObject;

@Service
public class GoogleMapsService {
    @Value("${google.maps.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public Double calcularDistancia(String origen, String destino) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origen + "&destination=" + destino + "&key=" + apiKey;
        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        JSONObject json = new JSONObject(response);
        if (json.has("routes") && json.getJSONArray("routes").length() > 0) {
            JSONObject leg = json.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0);
            double metros = leg.getJSONObject("distance").getDouble("value");
            return metros / 1000.0;
        }
        return null;
    }
}

