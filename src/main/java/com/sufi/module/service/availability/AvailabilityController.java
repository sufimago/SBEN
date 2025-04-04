package com.sufi.module.service.availability;

import com.sufi.commons.IProcessorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class AvailabilityController {

    @Autowired
    private IProcessorClient processorClient;

    @GetMapping("/obtenerDisponibilidad")
    public Mono<List<AvailabilityResponse>> obtenerDisponibilidad(
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam List<Integer> listingIds,
            @RequestParam Integer occupancy
    ) {
        Instant startTime = Instant.now();
        AvailabilityRequest request = construirDisponibilidadRequest(fechaEntrada, fechaSalida, listingIds, occupancy);
        return processorClient.getDisponibilidad(request)
                .doOnSubscribe(subscription -> System.out.println("Iniciando la petición..."))
                .doOnSuccess(response -> {
                    Duration duration = Duration.between(startTime, Instant.now());
                    System.out.println("Petición completada en: " + duration.toMillis() + " ms");
                });
    }

    public AvailabilityRequest construirDisponibilidadRequest(String fechaEntrada, String fechaSalida, List<Integer> listingIds, Integer occupancy) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate = LocalDate.parse(fechaEntrada, formatter).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(fechaSalida, formatter).atStartOfDay();

        return new AvailabilityRequest(startDate, endDate, listingIds, occupancy);
    }

    @GetMapping("/obtenerAlojamientos")
    public Mono<String> obtenerAlojamientos() {
        return processorClient.getAlojamientos();
    }


}
