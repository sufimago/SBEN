package com.sufi.module.service.availability;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AvailabilityService {

    private final Map<String, AvailabilityResponse> availabilityStore = new ConcurrentHashMap<>();

    // Método para guardar las respuestas originales (se llama cuando se genera la disponibilidad)
    public void storeAvailability(AvailabilityResponse response) {
        availabilityStore.put(response.getKeyOption(), response);
    }

    // Método para recuperar los datos originales
    public Mono<AvailabilityResponse> getOriginalAvailability(String keyOption) {
        return Mono.justOrEmpty(availabilityStore.get(keyOption))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Disponibilidad no encontrada")));
    }
}